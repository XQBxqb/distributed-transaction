package cn.distributed.transaction.controller;

import cn.distributed.transaction.dataobj.UndoLog;
import cn.distributed.transaction.dto.RollbackDto;
import cn.distributed.transaction.service.BookService;
import cn.distributed.transaction.service.UndoLogService;
import cn.distributed.transaction.thread.BTLocal;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServiceoBookController {
    private final BookService bookService;
    private final UndoLogService undoLogService;

    @GetMapping("/test/deletes")
    public void testDeletes(){
        BTLocal.setRollbackStatus();
        UndoLog undoLog = undoLogService.getUndoLog(new RollbackDto("7f74ed6d-b51f-46c1-aa49-c02797205dc8", "0", "delete"));
        String jsonData = new String(undoLog.getLogInfoBefore(), StandardCharsets.UTF_8);
        List list = JSONUtil.parse(jsonData)
                            .toBean(List.class);
        list.forEach(t-> System.out.println(JSONUtil.parse(t)));
        undoLogService.rollbackDelete(undoLog,undoLog.getXid(),undoLog.getBranchId());
    }
}
