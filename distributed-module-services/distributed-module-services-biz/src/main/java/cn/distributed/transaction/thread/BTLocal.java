package cn.distributed.transaction.thread;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

public class BTLocal {

    private static ThreadLocal<BTStatus> threadLocal = new ThreadLocal<>();

    public static void setThreadLocal(BTStatus btStatus) {
        threadLocal.set(btStatus);
    }

    public static void setRollbackStatus(){
        threadLocal.set(new BTStatus(null,null,Boolean.TRUE,null,null,null,null));
    }

    public static String getXid(){
        return threadLocal.get().getXid();
    }

    public static String getBid(){
        return threadLocal.get().getBid();
    }

    public static Boolean getPreStatus(){
        return threadLocal.get().getPreStatus();
    }

    public static void setIds(List<Object> ids){
        threadLocal.get().setIds(ids);
    }

    public static List<Object> getIds(){
        return threadLocal.get().getIds();
    }

    public static void updatePreStatus(){
        threadLocal.get().setPreStatus(Boolean.TRUE);
    }

    public static void remove(){
        threadLocal.remove();
    }

    public static Integer getOrder(){
        return threadLocal.get().getOrder();
    }

    public static void setBeforeImages(byte[] bytes){
        threadLocal.get().setBeforeImages(bytes);
    }

    public static byte[] getBeforeImages(){
        return threadLocal.get().getBeforeImages();
    }

    public static void setSqlType(String type){
        threadLocal.get().setSqlType(type);
    }

    public static String getSqlType(){
        return threadLocal.get().getSqlType();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BTStatus{
        public static BTStatus initStatus(String bid,String xid,Integer order){
            return new BTStatus(xid,bid,Boolean.FALSE,null,order,null,null);
        }

        private String xid;
        private String bid;
        //状态为0：预操作未进行；状态为1：预操作成功，
        private Boolean preStatus;

        private List<Object> ids;

        private Integer order;

        private byte[] beforeImages;

        private String sqlType;
    }
}
