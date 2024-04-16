package cn.distributed.transaction.exception.enums;

public enum SysStatusEnum implements BaseStatusEnum{
    RES_SUCCESS(200,"success","success");
    private Integer code;
    private String message;
    private String success;

    SysStatusEnum(Integer code, String message,String success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }
    @Override
    public Integer getCode() {
        return code;
    }
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getSuccess() {
        return success;
    }
}
