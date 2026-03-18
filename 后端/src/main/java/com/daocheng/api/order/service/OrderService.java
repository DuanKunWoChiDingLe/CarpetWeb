package com.daocheng.api.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.order.dto.request.OrderCreateRequest;
import com.daocheng.api.order.dto.response.OrderResponse;
import com.daocheng.api.order.entity.Order;

public interface OrderService extends IService<Order> {
    OrderResponse createOrder(Long userId, String userName, OrderCreateRequest request);
    IPage<OrderResponse> getUserOrders(Long userId, String status, Page<Order> page);
    OrderResponse getOrderDetail(Long orderId, Long userId);
    void cancelOrder(Long orderId, Long userId);
    OrderResponse payOrder(Long orderId, Long userId, String password);
    void confirmReceipt(Long orderId, Long userId);

    // 商家端
    IPage<OrderResponse> getAllOrders(String status, String keyword, String startDate, String endDate, Page<Order> page);
    void shipOrder(Long orderId);
    void merchantCancelOrder(Long orderId);
}