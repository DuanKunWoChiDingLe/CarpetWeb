package com.daocheng.api.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "品牌名称不能为空")
    private String name;
    private String logo;
    private Integer sortOrder;
}