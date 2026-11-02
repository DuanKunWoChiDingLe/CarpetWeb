package com.daocheng.api.cart.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;
}