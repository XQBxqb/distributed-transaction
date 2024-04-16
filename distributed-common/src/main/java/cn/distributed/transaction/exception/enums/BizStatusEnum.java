package cn.distributed.transaction.exception.enums;

/**
 * @author 昴星
 * @date 2023-09-02 22:12
 * @explain
 */

public enum BizStatusEnum implements BaseStatusEnum{

    RES_SUCCESS(200,"success","success"),
    TRANSACTION_ERROR_COLUMN_EXIST_OTHER_BT(401,"事务处理数据被其他事物管理，请重新开始事务","false"),
    UPDATE_ERROR_OF_KEY_BLANK(402,"更新失败，字段主键为空","false"),

    TRANSACTION_ERROR_TX_ROLLBACK(403,"分支事务处理异常，进行回滚操作","false")
    ;




    private Integer code;
    private String message;

    private String success;

    BizStatusEnum(Integer code, String message,String success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }
    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public String getSuccess() {
        return success;
    }
}
