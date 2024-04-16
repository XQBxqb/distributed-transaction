package cn.distributed.transaction.tx.thread;

import cn.distributed.transaction.dto.InvocationDto;
import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TXLocal {

    private static final ThreadLocal<TXStatus> txStatusThreadLocal = new ThreadLocal<>();

    /**
     * 初始化TX以及BT分支集
     */
    public static void initTX(List<InvocationDto> invocationDtos){
        List<InvocationDto> list = invocationDtos.stream()
                                                    .sorted(Comparator.comparing(InvocationDto::getOrder))
                                                    .collect(Collectors.toList());
        AtomicInteger number = new AtomicInteger(0);
        List<BT> bts = list.stream()
                               .map(t -> BT.initBuildBT(t.getOrder(), String.valueOf(number.getAndIncrement())))
                               .collect(Collectors.toList());
        txStatusThreadLocal.set(TXStatus.initBuildTX(bts));
    }

    public static List<BT> getBTs(){
        return txStatusThreadLocal.get()
                                  .getBts();
    }

    /**
     * 顺序执行BT，获取要执行BT的顺序索引
     * 例如：现在执行完第一个BT要执行但还没执行第二个BT，那么这个就返回1(第二个)
     */
    public static String getTurnBid(){
        Integer turn = txStatusThreadLocal.get()
                                          .getTurn();
        return txStatusThreadLocal.get().getBts().get(turn).getBid();
    }

    /**
     * 执行完一个BT，更新计数器
     */
    public static void updateTurn(){
        txStatusThreadLocal.get().setTurn(txStatusThreadLocal.get().getTurn()+1);
    }

    /**
     * 请完成一次tx删除这里对象的缓存
     */
    public static void remove(){
        if(txStatusThreadLocal.get()!=null)
            txStatusThreadLocal.remove();
    }

    public static String getXid(){
        return txStatusThreadLocal.get().getXid();
    }

    public static Integer getTurnOrder(){
        return txStatusThreadLocal.get().getBts().get(getTurn()).getOrder();
    }

    public static Integer getTurn(){
        return txStatusThreadLocal.get().getTurn();
    }


    /**
     * 执行完一个BT，更新加锁的数据以及操作类型
     */
    public static void afterBTUpdate(List<String> lockIds,String type){
        txStatusThreadLocal.get().getBts().get(getTurn()).setType(type);
        txStatusThreadLocal.get().getBts().get(getTurn()).setLockIds(lockIds);
    }

    public static void updateRollbackStatus(){
        txStatusThreadLocal.get().setLogStatus(Boolean.TRUE);
    }

    @Data
    @AllArgsConstructor
    public static class TXStatus{
        private String xid;

        private List<BT> bts;

        private Boolean logStatus;

        //要执行BT的顺序索引
        private Integer turn;

        public static TXStatus initBuildTX(List<BT> bts){
            return new TXStatus(IdUtil.randomUUID(),bts,Boolean.FALSE,0);
        }
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BT{
        private Integer order;

        private String bid;

        private List<String> lockIds;

        private String type;

        public static BT initBuildBT(Integer order,String bid){
            return new BT(order,bid,null,null);
        }

    }
}
