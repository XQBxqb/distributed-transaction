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

}
