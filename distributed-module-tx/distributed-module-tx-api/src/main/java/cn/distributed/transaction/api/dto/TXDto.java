package cn.distributed.transaction.api.dto;

import cn.distributed.transaction.dto.InvocationDto;

import java.util.List;

public class TXDto {
    public TXDto(List<InvocationDto> invocationDtoList) {
        this.invocationDtoList = invocationDtoList;
    }

    public TXDto() {
    }

    private List<InvocationDto> invocationDtoList;

    public List<InvocationDto> getInvocationDtoList() {
        return invocationDtoList;
    }

    public void setInvocationDtoList(List<InvocationDto> invocationDtoList) {
        this.invocationDtoList = invocationDtoList;
    }
}
