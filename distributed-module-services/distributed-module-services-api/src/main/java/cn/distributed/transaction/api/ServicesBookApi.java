package cn.distributed.transaction.api;

import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.res.RestRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = ServicesBookApi.SERVERNAME)
public interface ServicesBookApi {
    static final String SERVERNAME = "book";

    static final String PREFIX = "/api/services/book";

    @GetMapping("feign"+PREFIX+"/addBook")
    public RestRes<?> addBook(BookDto bookDto);
}
