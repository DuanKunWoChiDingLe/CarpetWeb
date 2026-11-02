package com.daocheng.api.cart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String name;
    private BigDecimal price;       // 当前价格（可能变化，但建议存快照？这里暂时返回商品当前价格）
    private Integer quantity;
    private String image;           // 商品主图
}