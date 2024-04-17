package cn.distributed.transaction.api;

import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.dto.RollbackDto;
import cn.distributed.transaction.res.RestRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = ServicesBookApi.SERVERNAME)
public interface ServicesBookApi {
    static final String SERVERNAME = "services";

    static final String PREFIX = "/api/services/book";

    @PostMapping("/feign"+PREFIX+"/addBook")
    public RestRes<?> addBook(@RequestBody BookDto bookDto);

    @PostMapping("/feign"+PREFIX+"/updateBook")
    public RestRes<?> updateBook(@RequestBody BookDto bookDto);

    @PostMapping("/feign"+PREFIX+"/deleteBook")
    public RestRes<?> deleteBook(String id);

    @PostMapping("/feign"+PREFIX+"/deleteBooks")
    public RestRes<?> deleteBooks(@RequestBody List<String> id);

    //使用feign接口传递List，用@RequestBody
    @PostMapping("/feign"+PREFIX+"/addBooks")
    public RestRes<?> addBooks(@RequestBody List<BookDto> bookDtoList);
    @PostMapping("/feign"+PREFIX+"/updateBooksNumberDes")
    public RestRes<?> updateBooksNumberDes(Integer number);

    @PostMapping("/rollback")
    public RestRes<?> rollback(@RequestBody RollbackDto rollbackDto);
}
