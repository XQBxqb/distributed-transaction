package cn.distributed.transaction.tx.api;

import cn.distributed.transaction.api.TXApi;
import cn.distributed.transaction.api.dto.TXDto;
import cn.distributed.transaction.dto.InvocationDto;
import cn.distributed.transaction.dto.RollbackDto;
import cn.distributed.transaction.exception.BizException;
import cn.distributed.transaction.exception.enums.BizStatusEnum;
import cn.distributed.transaction.res.BTRes;
import cn.distributed.transaction.res.RestRes;
import cn.distributed.transaction.tx.config.RedissonService;
import cn.distributed.transaction.tx.consts.TXConsts;
import cn.distributed.transaction.tx.rpc.FeignApi;
import cn.distributed.transaction.tx.thread.TXLocal;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TXApiImpl implements TXApi {
    private final FeignApi feignApi;

    private final RedissonService redissonService;
    //无论事务链是否最终执行成功，要保证数据最终一致，服务要么全部完成，要么回滚；锁最终一定要释放
    @Override
    public RestRes<?> tx(TXDto txDto) {
        List<InvocationDto> invocationDtos = txDto.getInvocationDtoList();

        //初始化tx
        TXLocal.initTX(invocationDtos);

        for(InvocationDto invocationDto : invocationDtos){
            RestRes<?> restRes = null;
            try {
                if((restRes =(feignApi.rpcReq(invocationDto))) == null || !restRes.getCode().equals(200))
                    throw new RuntimeException();
            } catch (Exception e) {
                log.error("Exception : "+e.getMessage());
                log.error("事务链: "+TXLocal.getXid()+" 事务处理异常，进行回滚操作 ");
                rollback(invocationDtos);
                //分布式事务结束，清理线程缓存
                TXLocal.remove();
                throw new BizException(BizStatusEnum.TRANSACTION_ERROR_TX_ROLLBACK);
            }
            BTRes btRes = JSONUtil.parse(restRes.getData()).toBean(BTRes.class);
            TXLocal.afterBTUpdate(btRes.getLockIds(),btRes.getType());
            TXLocal.updateTurn();
        }

        unLock(TXLocal.getBTs());
        //分布式事务结束，清理线程缓存
        TXLocal.remove();
        return RestRes.ok();
    }
    //回滚要逆序进行，否则会对数据脏写
    private void rollback(List<InvocationDto> invocationDtos){
        TXLocal.updateRollbackStatus();
        TXLocal.BT[] bts = TXLocal.getBTs().toArray(new TXLocal.BT[]{});
        String xid = TXLocal.getXid();
        for(int i=TXLocal.getTurn()-1;i>=0;i--){
            InvocationDto invocationDto = invocationDtos.get(i);
            String interfaceName = invocationDto.getInterfaceName();

            RestRes restRes = feignApi.rpcReq(new InvocationDto(interfaceName, TXConsts.ROLLBACK_INTERFACE_METHOD_NAME, new Object[]{new RollbackDto(xid, bts[i].getBid(), bts[i].getType())}, new Class[]{RollbackDto.class}, 0));
            //如果回滚失败，应该发送错误日志到消息队列中，进行人工补偿

            //回滚事务后解行锁
            List<String> lockIds = bts[i].getLockIds();
            lockIds.forEach(t->redissonService.deleteIngoreNotExists(TXConsts.PREFIX_REDIS_COLUMN_KEY_LOCK+"-"+t));

        }
        log.info("回滚完毕");
    }

    //从后向前解锁，实现上来讲从前往后效果是一样的
    private void unLock(List<TXLocal.BT> bts){
        for(int i=bts.size()-1;i>=0;i--){
            bts.get(i).getLockIds().forEach(t->redissonService.deleteIngoreNotExists(TXConsts.PREFIX_REDIS_COLUMN_KEY_LOCK+"-"+t));
        }
    }
}
