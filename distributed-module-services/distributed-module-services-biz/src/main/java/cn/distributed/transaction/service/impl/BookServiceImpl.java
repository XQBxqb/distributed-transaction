package cn.distributed.transaction.service.impl;

import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.dataobj.Book;
import cn.distributed.transaction.exception.BizException;
import cn.distributed.transaction.exception.enums.BizStatusEnum;
import cn.distributed.transaction.mapper.BookMapper;
import cn.distributed.transaction.service.BookService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBooks(List<BookDto> list) {
        List<Book> books = list.stream()
                                 .map(t -> {
                                     Book book = new Book();
                                     BeanUtil.copyProperties(t, book);
                                     return book;
                                 })
                                 .collect(Collectors.toList());
        bookMapper.batchInsert(books);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBook(BookDto bookDto) {
        Book book = new Book();
        BeanUtil.copyProperties(bookDto,book);
        System.out.println(JSONUtil.parse(book));
        bookMapper.insertBook(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(BookDto bookDto) {
        if(StrUtil.isBlank(bookDto.getId())){
            throw new BizException(BizStatusEnum.UPDATE_ERROR_OF_KEY_BLANK);
        }
        Book book = new Book();
        BeanUtil.copyProperties(bookDto,book);
        bookMapper.updateById(book);
    }

    @Override
    public List<Book> selectUpdateLists(List<Object> list) {
        return bookMapper.selectBatchIds(list.stream().map(t->(String)t).collect(Collectors.toList())) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(String id) {
        bookMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBooks(List<String> ids) {
        bookMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBookNumberDes(Integer number) {
        bookMapper.updateNumber(number,21);
    }

    @Override
    public List<String> selectIds() {
        return bookMapper.selectIds();
    }
}
