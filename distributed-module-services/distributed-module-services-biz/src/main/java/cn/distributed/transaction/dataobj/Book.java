package cn.distributed.transaction.dataobj;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("book")
@Data
public class Book {
    private String id;

    private String name;

    private Integer cost;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer number;
}
