package com.daocheng.api.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long seriesId;
    private String colorCode;
    private String name;
    private BigDecimal pricePerSqm;
    private Integer stock;
    private String images;  // 存储JSON数组，例如 ["/uploads/1.jpg","/uploads/2.jpg"]
    private String description;
    private Integer sales;  // 销量
    private Integer status; // 0下架 1上架

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private Series series;
}