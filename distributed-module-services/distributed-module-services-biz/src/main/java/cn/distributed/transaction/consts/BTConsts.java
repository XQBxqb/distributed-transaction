package cn.distributed.transaction.consts;

public class BTConsts {
    public static final String PREFIX_REDIS_COLUMN_KEY_LOCK = "PREFIX_REDIS_COLUMN_KEY_LOCK";

    public static final String FEIGN_HEADER_XID_NAME = "xid";
    public static final String FEIGN_HEADER_ORDER_NAME = "order";
    public static final String FEIGN_HEADER_BID_NAME = "bid";

    public static final Integer DEFAULT_REDIS_KEY_TIME = 60;
}
