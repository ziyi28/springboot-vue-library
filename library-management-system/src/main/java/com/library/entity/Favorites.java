package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorites")
public class Favorites {

    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime createTime;
}