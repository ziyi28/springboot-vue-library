package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("borrow_records")
public class BorrowRecord extends BaseEntity {

    private Long userId;
    private Long bookId;
    private Long bookRecordId;
    private Long adminId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Integer renewCount;
    private Integer status;
    private BigDecimal fineAmount;
    private String remark;
}