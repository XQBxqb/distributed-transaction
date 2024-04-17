package cn.distributed.transaction.dataobj;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@TableName(value = "undo_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UndoLog {

    private String xid;

    private String branchId;
    private Boolean logStatus;
    private byte[] logInfoBefore;
    private byte[] logInfoAfter;
    private Integer order;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
