package cn.distributed.transaction.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BTRes {

    private List<String> lockIds;

    private String undoLogXIdBId;

    private String type;

    public static BTRes buildInsert(List<String> ids,String undoLogXIdBId,String type){
        return new BTRes(ids,undoLogXIdBId,type);
    }

    public static BTRes buildUpdate(List<String> ids,String undoLogXIdBId,String type){
        return new BTRes(ids,undoLogXIdBId,type);
    }

    public static BTRes buildDelete(String undoLogXIdBId,String type){
        return new BTRes(null,undoLogXIdBId,type);
    }
}
