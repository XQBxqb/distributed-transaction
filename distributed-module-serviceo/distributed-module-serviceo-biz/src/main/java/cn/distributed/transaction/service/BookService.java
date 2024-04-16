package cn.distributed.transaction.service;

import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.dataobj.Book;

import java.util.List;


public interface BookService {
    void insertBooks(List<BookDto> list);

    void insertBook(BookDto bookDto);


    void updateBook( BookDto bookDto);

     List<Book> selectUpdateLists(List<Object> list);

     void deleteBook(String id);
     void deleteBooks(List<String> ids);
     void updateBookNumberDes(Integer number);

    List<String> selectIds();
}
