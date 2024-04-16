package cn.distributed.transaction.api;

import cn.distributed.transaction.api.dto.TXDto;
import cn.distributed.transaction.res.RestRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = TXApi.SERVERNAME)
public interface TXApi {
    static final String SERVERNAME = "tx";

    static final String PREFIX = "/api";

    @PostMapping("feign"+PREFIX+"/tx")
    public RestRes<?> tx(@RequestBody TXDto bookDto);
}
