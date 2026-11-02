package com.daocheng.api.banner.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BannerResponse {
    private Long id;
    private String imageUrl;
    private String linkType;
    private String linkValue;
    private Integer sortOrder;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}