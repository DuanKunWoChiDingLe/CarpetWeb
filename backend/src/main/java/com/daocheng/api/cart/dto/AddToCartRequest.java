package com.daocheng.api.cart.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class AddToCartRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;
}