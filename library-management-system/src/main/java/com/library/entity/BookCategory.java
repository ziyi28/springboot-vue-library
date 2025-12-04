package com.library.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.library.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("book_categories")
public class BookCategory extends BaseEntity {

    private String categoryName;
    private String categoryCode;
    private String description;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;
}