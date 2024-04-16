package cn.distributed.transaction.exception;




import cn.distributed.transaction.exception.enums.BizStatusEnum;
import lombok.Data;

/**
 * @author 昴星
 * @date 2023-10-05 22:27
 * @explain
 */
@Data
public class BizException extends BaseException{
    public BizException(BizStatusEnum statusEnum){
        super(statusEnum);
    }
}
