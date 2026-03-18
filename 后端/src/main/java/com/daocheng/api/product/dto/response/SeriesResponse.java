package com.daocheng.api.product.dto.response;

import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;
import lombok.Data;

@Data
public class SeriesResponse {
    private Long id;
    private Long brandId;
    private String brandName;  // 品牌名称
    private String name;
    private String coverImage;
    private LayTypeEnum layType;
    private MaterialEnum material;
    private String spec;
    private String description;
    private Integer colorCount; // 系列下色号数量（可额外统计）
}