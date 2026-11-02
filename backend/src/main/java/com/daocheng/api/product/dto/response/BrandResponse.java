package com.daocheng.api.product.dto.response;

import lombok.Data;

@Data
public class BrandResponse {
    private Long id;
    private String name;
    private String logo;
    private Integer sortOrder;
}