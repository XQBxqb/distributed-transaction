package cn.distributed.transaction.service;

import cn.distributed.transaction.dataobj.UndoLog;
import cn.distributed.transaction.dto.RollbackDto;
import cn.distributed.transaction.res.RestRes;

public interface UndoLogService {
    void insertUndoLog(UndoLog undoLog);

    UndoLog getUndoLog(RollbackDto rollbackDto);

    RestRes rollbackUpdate(UndoLog undoLog,String xid, String branchId);

    RestRes rollbackInsert(UndoLog undoLog,String xid, String branchId);

    RestRes rollbackDelete(UndoLog undoLog,String xid, String branchId);

}
