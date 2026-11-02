package com.daocheng.api.banner.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class BannerStatusRequest {
    @NotNull(message = "状态不能为空")
    private Boolean enabled;
}