package com.daocheng.api.order.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private BigDecimal totalAmount;
    private String status;
    private String consignee;
    private String phone;
    private String address;
    private String remark;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}

