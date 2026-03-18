package com.daocheng.api.product.dto.response;

import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private Long seriesId;
    private String seriesName;
    private String brandName;
    private LayTypeEnum layType;
    private MaterialEnum material;
    private String spec;
    private String colorCode;
    private String name;
    private BigDecimal pricePerSqm;
    private Integer stock;
    private List<String> images;
    private String description;
    private Integer status;
    private Integer sales;
}