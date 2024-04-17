package cn.distributed.transaction.controller;

import cn.distributed.transaction.api.ServiceoBookApi;
import cn.distributed.transaction.api.TXApi;
import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.api.dto.TXDto;
import cn.distributed.transaction.dto.InvocationDto;
import cn.distributed.transaction.res.RestRes;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private final TXApi txApi;
    

    @GetMapping("/client/service")
    public RestRes clientService(){
        ArrayList<InvocationDto> invocationDtos = new ArrayList<>();
        //添加一个book
        invocationDtos.add(new InvocationDto(ServiceoBookApi.class.getName(),"addBook",new Object[]{new BookDto(IdUtil.randomUUID(),"math",1, LocalDateTime.now(),LocalDateTime.now(),1)},new Class[]{BookDto.class},0));
        //添加多个book
        List<BookDto> bookDtos = new ArrayList<>();
        bookDtos.add(new BookDto(IdUtil.randomUUID(),"math5",1,LocalDateTime.now(),LocalDateTime.now(),10));
        bookDtos.add(new BookDto(IdUtil.randomUUID(),"math6",1,LocalDateTime.now(),LocalDateTime.now(),13));
        bookDtos.add(new BookDto(IdUtil.randomUUID(),"math7",1,LocalDateTime.now(),LocalDateTime.now(),1));
        bookDtos.add(new BookDto(IdUtil.randomUUID(),"math8",1,LocalDateTime.now(),LocalDateTime.now(),1));
        invocationDtos.add(new InvocationDto(ServiceoBookApi.class.getName(),"addBooks",new Object[]{bookDtos},new Class[]{List.class},1));
        //删除多个book
        List<String> deletesIds = new ArrayList<>();
        deletesIds.add("009e65d6-207e-4940-a7fb-c48c22751c01");
        deletesIds.add("00b736f6-6fa4-43bb-8223-2ae99024f6c5");
        deletesIds.add("01132360-e8c1-4d9a-8eda-8b04bb0c5d67");
        deletesIds.add("01d7e4a8-8fe6-4fa6-be10-2ac3be1d926e");
        deletesIds.add("01de8a44-5a6d-47e8-a65d-4a358c1fa27a");
        invocationDtos.add(new InvocationDto(ServiceoBookApi.class.getName(),"deleteBooks",new Object[]{deletesIds},new Class[]{List.class},2));
        //更新单个book
        //更新多个book
        RestRes<?> tx = txApi.tx(new TXDto(invocationDtos));

        log.info("finish tx ");
        return tx;
    }
}
