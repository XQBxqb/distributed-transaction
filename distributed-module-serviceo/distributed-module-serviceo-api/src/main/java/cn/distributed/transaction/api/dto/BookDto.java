package cn.distributed.transaction.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private String id;

    private String name;

    private Integer cost;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer number;
}
