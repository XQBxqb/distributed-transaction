package cn.distributed.transaction.api;

import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.res.RestRes;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServicesBookApiImpl implements ServicesBookApi {
    @Override
    public RestRes<?> addBook(BookDto bookDto) {
        return null;
    }
}
