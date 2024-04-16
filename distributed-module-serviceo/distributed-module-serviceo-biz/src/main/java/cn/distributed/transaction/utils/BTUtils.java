package cn.distributed.transaction.utils;

import cn.distributed.transaction.consts.BTConsts;
import cn.distributed.transaction.dataobj.Book;
import cn.distributed.transaction.dataobj.UndoLog;
import cn.distributed.transaction.thread.BTLocal;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class BTUtils {

    public static String parseUpdateSql(String sql){
        String[] split = sql.split("\\s+");
        StringBuilder afterParse = new StringBuilder("select * from "+split[1]);
        for(int i=2;i<split.length;i++){
            if(split[i].equalsIgnoreCase("where")){
                afterParse.append(" "+split[i++]);
                for(;i<split.length;i++)
                    afterParse.append(" "+split[i]);
            }
        }
        return afterParse.toString();
    }

    public static String parseSelectSql(String sql){
        String[] split = sql.split("\\s+");
        return "select id from "+split[2];
    }

    public static String parseDeleteSql(String sql){
        String[] split = sql.split("\\s+");
        StringBuilder stringBuilder = new StringBuilder(split[1]);
        for(int i=2;i<split.length;i++)
            stringBuilder.append(" "+split[i]);
        return "select * "+stringBuilder.toString();
    }

    @SneakyThrows
    public static UndoLog getUpdateUndoLog(List<Book> books) {
        UndoLog undoLog;

        undoLog = new UndoLog(BTLocal.getXid(), BTLocal.getBid(), Boolean.FALSE, BTLocal.getBeforeImages(), JSONUtil.toJsonStr(books)
                                                                                                                    .getBytes(StandardCharsets.UTF_8), BTLocal.getOrder(), LocalDateTime.now(), LocalDateTime.now());
        return undoLog;
    }

    public static UndoLog getDeleteUndoLog() {
        UndoLog undoLog;
        undoLog = new UndoLog(BTLocal.getXid(), BTLocal.getBid(), Boolean.FALSE, BTLocal.getBeforeImages(), null,
                BTLocal.getOrder(), LocalDateTime.now(), LocalDateTime.now());
        return undoLog;
    }

    @SneakyThrows
    public static UndoLog getInsertUndoLog(List<String> ids) {
        UndoLog undoLog;
        undoLog = new UndoLog(BTLocal.getXid(), BTLocal.getBid(), Boolean.FALSE, null, JSONUtil.toJsonStr(ids).getBytes(StandardCharsets.UTF_8),
                BTLocal.getOrder(), LocalDateTime.now(), LocalDateTime.now());
        return undoLog;
    }

}
