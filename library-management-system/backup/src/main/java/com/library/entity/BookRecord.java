package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("book_records")
public class BookRecord extends BaseEntity {

    private Long bookId;
    private String barcode;
    private String location;
    private Integer status;
    private LocalDate purchaseDate;
    private BigDecimal price;
}