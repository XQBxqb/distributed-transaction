package cn.distributed.transaction.tx.rpc;


import cn.distributed.transaction.api.ServiceoBookApi;
import cn.distributed.transaction.dto.InvocationDto;
import cn.distributed.transaction.res.RestRes;
import cn.distributed.transaction.tx.utils.SpringContextUtil;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 首先能够根据全类名可以动态获取对应api的Bean，然后能够通过这个对象执行对应的方法
 */

@Component
@EnableFeignClients(clients = {ServiceoBookApi.class})
public class FeignApi {
    private static HashMap<String,Class<?>> apiHashMap = new HashMap<>();

    @PostConstruct
    public void init(){
        Class<?>[] clients = FeignApi.class.getAnnotation(EnableFeignClients.class)
                                           .clients();
        for(Class<?> cl : clients)
            apiHashMap.put(cl.getName(),cl);

    }
    @SneakyThrows
    public RestRes rpcReq(InvocationDto invocationDto){
        RestRes restRes;
        Class<?> cla = apiHashMap.get(invocationDto.getInterfaceName());
        Object obj = SpringContextUtil.getBean(cla);
        Method method = cla.getMethod(invocationDto.getMethodsName(),invocationDto.getParamsType());

        Class<?>[] paramsType = invocationDto.getParamsType();
        Object[] initialParams = invocationDto.getParms();
        Object[] parseParams = new Object[paramsType.length];

        for(int i=0;i< initialParams.length;i++){
            parseParams[i]=JSONUtil.parse(JSONUtil.toJsonStr(initialParams[i])).toBean(paramsType[i]);
        }
         restRes = (RestRes) method.invoke(obj,parseParams);
        return restRes;
    }
}
