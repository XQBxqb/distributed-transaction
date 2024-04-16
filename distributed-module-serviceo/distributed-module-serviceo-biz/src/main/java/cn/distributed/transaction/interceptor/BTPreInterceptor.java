package cn.distributed.transaction.interceptor;

import cn.distributed.transaction.config.RedissonService;
import cn.distributed.transaction.consts.BTConsts;
import cn.distributed.transaction.exception.BizException;
import cn.distributed.transaction.exception.enums.BizStatusEnum;
import cn.distributed.transaction.thread.BTLocal;
import cn.distributed.transaction.utils.BTUtils;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 一次bt事务进行仅仅第一次非select sql会进行拦截
 */
@Component
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
@Slf4j
public class BTPreInterceptor implements Interceptor {
    @Autowired
    private RedissonService redissonService;

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if(!BTLocal.getPreStatus().equals(Boolean.FALSE))
            return invocation.proceed();
        BTLocal.updatePreStatus();
        switch (mappedStatement.getSqlCommandType()) {
            case INSERT:
                // 处理 INSERT 语句
                handleInsert(invocation,mappedStatement);
                break;
            case UPDATE:
                // 处理 UPDATE 语句
                handleUpdate(invocation,mappedStatement);
                break;
            case DELETE:
                // 处理 DELETE 语句
                handleDelete(invocation,mappedStatement);
        }
        return invocation.proceed();
    }

    /**
     * 拦截insert,前置镜像不需要，后置镜像仅仅存储idList就行，回滚时根据id删除
     */
    private void handleInsert(Invocation invocation,MappedStatement mappedStatement) throws InvocationTargetException, IllegalAccessException {
        String originalSql = resolveSqlWithParameters(invocation);

        // 打印解析后的 SQL
        log.info("Resolved SQL: {}", originalSql);
        String parseSelectSql = BTUtils.parseSelectSql(originalSql);

        List<Map<String, Object>> orginalLists = SqlRunner.db()
                                                          .selectList(parseSelectSql);

        List<Object> ids = orginalLists.stream()
                                      .map(t ->  t.get("id"))
                                      .collect(Collectors.toList());

        BTLocal.setSqlType("insert");
        BTLocal.setIds(ids);
    }

    /**
     * 既要获取前置镜像，又要获取后置镜像
     */
    @SneakyThrows
    private void handleUpdate(Invocation invocation,MappedStatement mappedStatement) throws InvocationTargetException, IllegalAccessException {
        String originalSql = resolveSqlWithParameters(invocation);

        // 打印解析后的 SQL
        log.info("Resolved SQL: {}", originalSql);

        //获得解析后SQL对应的select表示
        String selectSQL = BTUtils.parseUpdateSql(originalSql);


        //获取更新前镜像
        List<Map<String, Object>> orginalLists = SqlRunner.db()
                                                  .selectList(selectSQL);
        //获取镜像ids
        List<Object> ids = orginalLists.stream()
                                      .map(t -> t.get("id"))
                                      .collect(Collectors.toList());
        //进行redis查询，是否有数据被其他事物加锁
        for(Object id:ids){
            String idS = (String) id;
            String lockId = redissonService.get(BTConsts.PREFIX_REDIS_COLUMN_KEY_LOCK + "-" + idS, String.class);
            if(lockId != null && !lockId.equals(BTLocal.getXid()))
                throw new BizException(BizStatusEnum.TRANSACTION_ERROR_COLUMN_EXIST_OTHER_BT);
        }

        //如果数据没有被别的事务链拿到，那么就对这些数据线加锁
        ids.forEach(t->
                redissonService.set(BTConsts.PREFIX_REDIS_COLUMN_KEY_LOCK + "-" + t,BTLocal.getXid(),BTConsts.DEFAULT_REDIS_KEY_TIME)
        );

        //然后将这些加锁的ids放入到线程资源中
        BTLocal.setIds(ids);

        //将更新前镜像转为二进制
        byte[] preImages = JSONUtil.toJsonStr(orginalLists)
                               .getBytes(StandardCharsets.UTF_8);
        BTLocal.setBeforeImages(preImages);
        BTLocal.setSqlType("update");
    }

    /**
     * 仅仅获取前置镜像所有数据就可以，但是仍需要判断是否有所，没有后置镜像
     */
    @SneakyThrows
    private void handleDelete(Invocation invocation,MappedStatement mappedStatement) throws InvocationTargetException, IllegalAccessException {
        // 处理 DELETE 语句的逻辑
        String originalSql = resolveSqlWithParameters(invocation);

        // 打印解析后的 SQL
        log.info("Resolved SQL: {}", originalSql);

        String deleteSql = BTUtils.parseDeleteSql(originalSql);
        List<Map<String, Object>> lists = SqlRunner.db()
                                                  .selectList(deleteSql);
        List<Object> ids = lists.stream()
                               .map(t -> t.get("id"))
                               .collect(Collectors.toList());
        //查看是否有锁,但是不需要加锁，因为删除后之后数据别的事务链本身就拿不到
        for(Object id:ids){
            String idS = (String) id;
            String lockId = redissonService.get(BTConsts.PREFIX_REDIS_COLUMN_KEY_LOCK + "-" + idS, String.class);
            if(lockId != null && !lockId.equals(BTLocal.getXid()))
                throw new BizException(BizStatusEnum.TRANSACTION_ERROR_COLUMN_EXIST_OTHER_BT);
        }

        BTLocal.setBeforeImages(JSONUtil.toJsonStr(lists).getBytes(StandardCharsets.UTF_8));
        BTLocal.setSqlType("delete");
    }
    @SneakyThrows
    private String resolveSqlWithParameters(Invocation invocation)  {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];

        BoundSql boundSql = mappedStatement.getBoundSql(parameter);

        Configuration configuration = mappedStatement.getConfiguration();
        //获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        //获取当前的sql语句有绑定的所有parameterMapping属性
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //去除空格
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
         /*如果参数满足：org.apache.ibatis.type.TypeHandlerRegistry#hasTypeHandler(java.lang.Class<?>)
                    org.apache.ibatis.type.TypeHandlerRegistry#TYPE_HANDLER_MAP
                    * 即是不是属于注册类型(TYPE_HANDLER_MAP...等/有没有相应的类型处理器)
                     * */

            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                //装饰器，可直接操作属性值 ---》 以parameterObject创建装饰器
                //MetaObject 是 Mybatis 反射工具类，通过 MetaObject 获取和设置对象的属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                //循环 parameterMappings 所有属性
                for (ParameterMapping parameterMapping : parameterMappings) {
                    //获取property属性
                    String propertyName = parameterMapping.getProperty();
                    System.err.println("propertyName: "+propertyName);
                    //是否声明了propertyName的属性和get方法
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        //判断是不是sql的附加参数
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;

    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        }else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        System.err.println("获取值: "+value);
        return value;
    }



}
