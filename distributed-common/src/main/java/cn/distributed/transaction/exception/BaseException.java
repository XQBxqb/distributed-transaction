package cn.distributed.transaction.exception;


import cn.distributed.transaction.exception.enums.BaseStatusEnum;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public abstract class BaseException extends RuntimeException{
    private BaseStatusEnum baseStatusEnum;

    public BaseException(BaseStatusEnum baseStatusEnum) {

        super(baseStatusEnum.getMessage(),null,false,false);
        this.baseStatusEnum = baseStatusEnum;
    }

    public BaseStatusEnum getBaseStatusEnum(){
        return this.baseStatusEnum;
    }
}
