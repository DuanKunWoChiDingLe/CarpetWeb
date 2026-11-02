package com.daocheng.api.order.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PayRequest {
    @NotBlank(message = "支付密码不能为空")
    private String password;
}