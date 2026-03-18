package com.daocheng.api.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    @NotNull(message = "系列ID不能为空")
    private Long seriesId;
    @NotBlank(message = "色号不能为空")
    private String colorCode;
    private String name;  // 如果为空，后端自动生成（系列名+色号）
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于0")
    private BigDecimal pricePerSqm;
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;
    private List<String> images; // 图片URL列表
    private String description;
    private Integer status; // 默认1
}