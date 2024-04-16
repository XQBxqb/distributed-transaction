package cn.distributed.transaction.exception;


import cn.distributed.transaction.exception.enums.SysStatusEnum;

public class SysException extends BaseException{
    public SysException(SysStatusEnum statusEnum) {
        super(statusEnum);
    }
}
