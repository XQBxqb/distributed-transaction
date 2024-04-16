package cn.distributed.transaction.dataobj;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("book")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String id;

    private String name;

    private Integer cost;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer number;
}
