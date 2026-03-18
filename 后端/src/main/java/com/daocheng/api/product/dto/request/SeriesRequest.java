package com.daocheng.api.product.dto.request;

import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeriesRequest {
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;
    @NotBlank(message = "系列名称不能为空")
    private String name;
    private String coverImage;
    @NotNull(message = "铺设方式不能为空")
    private LayTypeEnum layType;
    @NotNull(message = "材质不能为空")
    private MaterialEnum material;
    @NotBlank(message = "规格不能为空")
    private String spec;
    private String description;
}