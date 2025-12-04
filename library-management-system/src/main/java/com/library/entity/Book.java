package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("books")
public class Book extends BaseEntity {

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private String description;
    private String coverImage;
    private Integer pageCount;
    private String language;
    private BigDecimal price;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer status;
}