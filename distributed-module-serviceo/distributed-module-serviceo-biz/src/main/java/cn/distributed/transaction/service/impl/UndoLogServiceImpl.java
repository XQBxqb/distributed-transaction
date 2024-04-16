package cn.distributed.transaction.service.impl;

import cn.distributed.transaction.dataobj.Book;
import cn.distributed.transaction.dataobj.UndoLog;
import cn.distributed.transaction.dto.RollbackDto;
import cn.distributed.transaction.mapper.BookMapper;
import cn.distributed.transaction.mapper.UndoLogMapper;
import cn.distributed.transaction.res.RestRes;
import cn.distributed.transaction.service.UndoLogService;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UndoLogServiceImpl implements UndoLogService {

    @Autowired
    private UndoLogMapper undoLogMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUndoLog(UndoLog undoLog) {
        undoLogMapper.insertUndoLog(undoLog);
    }

    @Override
    public UndoLog getUndoLog(RollbackDto rollbackDto) {
        UndoLog undoLog = undoLogMapper.selectUndoLog(rollbackDto.getXid(), rollbackDto.getBid());
        return undoLog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes rollbackUpdate(UndoLog undoLog,String xid, String branchId){
        List<Book> books = getBeforeImagesBooks(undoLog);
        books.forEach(t->bookMapper.updateById(t));
        undoLogMapper.updateUndoLogStatus(xid,branchId);
        return RestRes.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes rollbackInsert(UndoLog undoLog,String xid, String branchId){
        List<String> ids = getAfterImagesBookIds(undoLog);
        bookMapper.deleteBatchIds(ids);
        undoLogMapper.updateUndoLogStatus(xid,branchId);
        return RestRes.ok();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes rollbackDelete(UndoLog undoLog,String xid, String branchId){
        List<Book> books = getBeforeImagesBooks(undoLog);
        bookMapper.batchInsert(books);
        undoLogMapper.updateUndoLogStatus(xid,branchId);
        return RestRes.ok();
    }
    private static List<Book> getBeforeImagesBooks(UndoLog undoLog) {
        byte[] logInfoBefore = undoLog.getLogInfoBefore();
        String json = new String(logInfoBefore, StandardCharsets.UTF_8);
        ArrayList<Object> arrayList = (ArrayList<Object>) JSONUtil.parse(json).toBean(ArrayList.class);
        List<Book> books = arrayList.stream()
                                    .map(t -> JSONUtil.parse(t)
                                                      .toBean(Book.class))
                                    .collect(Collectors.toList());
        return books;
    }
    private static List<String> getAfterImagesBookIds(UndoLog undoLog) {
        byte[] getLogInfoAfter = undoLog.getLogInfoAfter();
        String jsonIds = new String(getLogInfoAfter, StandardCharsets.UTF_8);
        ArrayList<Object> bean = (ArrayList<Object>) JSONUtil.parse(jsonIds).toBean(ArrayList.class);
        List<String> ids = bean.stream()
                               .map(t -> (String)t)
                               .collect(Collectors.toList());
        return ids;
    }


}
