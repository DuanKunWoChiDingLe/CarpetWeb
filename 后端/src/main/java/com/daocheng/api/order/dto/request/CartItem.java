package com.daocheng.api.order.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CartItem {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    @NotNull(message = "购买数量不能为空")
    private Integer quantity;
}