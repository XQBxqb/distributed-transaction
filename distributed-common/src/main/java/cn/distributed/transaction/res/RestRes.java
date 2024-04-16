package cn.distributed.transaction.res;


import cn.distributed.transaction.exception.enums.BaseStatusEnum;
import cn.distributed.transaction.exception.enums.BizStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestRes<T> {
    private String msg;

    private Integer code;

    private T data;

    private String success;

    private RestRes() {
        this(BizStatusEnum.RES_SUCCESS);
    }

    private RestRes(T data) {
        this();
        this.data = data;
        success = "success";
    }

    private RestRes(Integer code, T data , String success){
        this.code=code;
        this.data=data;
        this.success = success;
    }

    private RestRes(BaseStatusEnum baseStatusEnum){
        msg= baseStatusEnum.getMessage();
        code= baseStatusEnum.getCode();
        this.success = baseStatusEnum.getSuccess();
    }

    public RestRes(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
        this.success = "false";
    }

    public static <T> RestRes<T> ok(){
        return new RestRes<>();
    }

    public static <T> RestRes<T> ok(T data){
        return new RestRes<>(data);
    }

    public static <T> RestRes<T> error(String msg,Integer code){
        return new RestRes<>(msg,code);
    }

    public static <T> RestRes<T> errors(String msg,Integer code,T data,String success){
        return new RestRes<T>(msg,code,data,success);
    }

    public static <T> RestRes<T> fileResp(Integer code,T data,String success){
        return new RestRes<T>(code,data,success);
    }

    public static <T> RestRes<T> errorEnum(BaseStatusEnum baseStatusEnum){
        return new RestRes<>(baseStatusEnum);
    }
}
