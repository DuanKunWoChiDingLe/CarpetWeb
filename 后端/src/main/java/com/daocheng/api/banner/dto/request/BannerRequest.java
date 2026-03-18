package com.daocheng.api.banner.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class BannerRequest {
    @NotBlank(message = "图片URL不能为空")
    private String imageUrl;
    @NotBlank(message = "链接类型不能为空")
    private String linkType; // "product" 或 "url"
    @NotBlank(message = "链接值不能为空")
    private String linkValue;
    @NotNull(message = "排序不能为空")
    private Integer sortOrder;
    private Boolean enabled; // 默认 true
}