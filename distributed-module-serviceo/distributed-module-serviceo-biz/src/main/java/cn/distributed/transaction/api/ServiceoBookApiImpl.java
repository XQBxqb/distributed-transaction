package cn.distributed.transaction.api;

import cn.distributed.transaction.api.dto.BookDto;
import cn.distributed.transaction.config.RedissonService;
import cn.distributed.transaction.consts.BTConsts;
import cn.distributed.transaction.dataobj.Book;
import cn.distributed.transaction.dataobj.UndoLog;
import cn.distributed.transaction.dto.RollbackDto;
import cn.distributed.transaction.exception.BizException;
import cn.distributed.transaction.exception.enums.BizStatusEnum;
import cn.distributed.transaction.res.BTRes;
import cn.distributed.transaction.res.RestRes;
import cn.distributed.transaction.service.BookService;
import cn.distributed.transaction.service.UndoLogService;
import cn.distributed.transaction.thread.BTLocal;
import cn.distributed.transaction.utils.BTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ServiceoBookApiImpl implements ServiceoBookApi {
    @Autowired
    private BookService bookService;

    @Autowired
    private RedissonService redissonService;
    @Autowired
    private UndoLogService undoLogService;


    @Override
    public RestRes<?> addBook(BookDto bookDto) {
        bookService.insertBook(bookDto);
        return RestRes.ok(suffixInsert());
    }

    @Override
    public RestRes<?> updateBook(BookDto bookDto) {
        bookService.updateBook( bookDto);
        return RestRes.ok(suffixUpdate());
    }

    @Override
    public RestRes<?> deleteBook(String id) {
        bookService.deleteBook(id);
        return RestRes.ok(suffixDelete());
    }

    @Override
    public RestRes<?> deleteBooks(List<String> ids) {
        //测试TX回滚有效性
        throw new BizException(BizStatusEnum.TRANSACTION_ERROR_COLUMN_EXIST_OTHER_BT);
        /*bookService.deleteBooks(ids);
        return RestRes.ok(suffixDelete());*/
    }

    
    @Override
    public RestRes<?> addBooks(List<BookDto> bookDtoList) {
        bookService.insertBooks(bookDtoList);
        return RestRes.ok(suffixInsert());
    }

    @Override
    public RestRes<?> updateBooksNumberDes(Integer number) {
        bookService.updateBookNumberDes(number);
        return RestRes.ok(suffixUpdate());
    }

    @Override
    public RestRes<?> rollback(RollbackDto rollbackDto) {
        UndoLog undoLog = undoLogService.getUndoLog(rollbackDto);
        String type = rollbackDto.getType();
        RestRes<?> restRes = null;
        switch (type){
            case "update":
                restRes=undoLogService.rollbackUpdate(undoLog,rollbackDto.getXid(),rollbackDto.getBid());
                break;
            case "insert":
                restRes=undoLogService.rollbackInsert(undoLog,rollbackDto.getXid(),rollbackDto.getBid());
                break;
            case "delete":
                restRes=undoLogService.rollbackDelete(undoLog,rollbackDto.getXid(),rollbackDto.getBid());
        }
        return restRes;
    }

    private BTRes suffixInsert() {
        //让现有的ids-过去的ids，就是插入的ids，然后对这些数据加锁，可能出现脏读问题，在脏读下回滚就会出现脏写
        List<String> list = bookService.selectIds();
        list.removeAll(BTLocal.getIds().stream().map(t->(String)t).collect(Collectors.toList()));
        //使用redis对这些ids进行加锁
        list.forEach(t-> redissonService.set(BTConsts.PREFIX_REDIS_COLUMN_KEY_LOCK+"-"+t, BTLocal.getXid(),BTConsts.DEFAULT_REDIS_KEY_TIME));
        UndoLog undoLog = BTUtils.getInsertUndoLog(list);
        undoLogService.insertUndoLog(undoLog);
        return BTRes.buildInsert(list,BTLocal.getXid() + "-" + BTLocal.getBid(),BTLocal.getSqlType());
    }

    private BTRes suffixDelete() {
        UndoLog undoLog = BTUtils.getDeleteUndoLog();
        undoLogService.insertUndoLog(undoLog);
        return  BTRes.buildDelete(BTLocal.getXid() + "-" + BTLocal.getBid(),BTLocal.getSqlType());
    }

    private BTRes suffixUpdate() {
        //先进行搜索更改后数据，然后进行插入undo_log
        List<Book> books = bookService.selectUpdateLists(BTLocal.getIds());
        UndoLog undoLog = BTUtils.getUpdateUndoLog(books);
        undoLogService.insertUndoLog(undoLog);
        //应该返回加锁的数据ids返回给tx，最终由tx一同锁的释放
        List<String> ids = BTLocal.getIds()
                                  .stream()
                                  .map(t -> (String) t)
                                  .collect(Collectors.toList());
        return BTRes.buildUpdate(ids, BTLocal.getXid() + "-" + BTLocal.getBid(),BTLocal.getSqlType());
    }
}
