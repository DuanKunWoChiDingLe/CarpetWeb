package com.daocheng.api.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("series")
public class Series {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long brandId;
    private String name;
    private String coverImage;
    private LayTypeEnum layType;
    private MaterialEnum material;
    private String spec;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private Brand brand;
}