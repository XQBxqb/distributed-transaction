package cn.distributed.transaction.mapper;

import cn.distributed.transaction.dataobj.UndoLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UndoLogMapper extends BaseMapper<UndoLog> {

    public void insertUndoLog( UndoLog undoLog);

    public UndoLog selectUndoLog(@Param("xid") String xid,@Param("branchId") String branchId);

    public void updateUndoLogStatus(@Param("xid") String xid,@Param("branchId") String branchId);

}
