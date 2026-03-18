package com.daocheng.api.order.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderCreateRequest {
    @NotBlank(message = "收货人不能为空")
    private String consignee;
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    @NotBlank(message = "收货地址不能为空")
    private String address;
    private String remark;

    @NotEmpty(message = "商品列表不能为空")
    private List<CartItem> items;
}