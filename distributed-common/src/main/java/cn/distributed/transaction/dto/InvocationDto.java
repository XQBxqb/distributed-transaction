package cn.distributed.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvocationDto {
    private String interfaceName;

    private String methodsName;

    private Object[] parms;

    private Class<?>[] paramsType;

    private Integer order;

}
