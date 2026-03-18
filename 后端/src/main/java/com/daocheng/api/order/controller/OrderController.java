package com.daocheng.api.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daocheng.api.common.dto.Result;
import com.daocheng.api.common.utils.CurrentUser;
import com.daocheng.api.order.dto.request.OrderCreateRequest;
import com.daocheng.api.order.dto.request.PayRequest;
import com.daocheng.api.order.dto.response.OrderResponse;
import com.daocheng.api.order.entity.Order;
import com.daocheng.api.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 创建订单
    @PostMapping
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long userId = CurrentUser.getId(); // 假设从 SecurityContext 获取
        String userName = CurrentUser.getUsername();
        OrderResponse response = orderService.createOrder(userId, userName, request);
        return Result.success(response);
    }

    // 分页查询当前用户订单
    @GetMapping
    public Result<IPage<OrderResponse>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Long userId = CurrentUser.getId();
        Page<Order> pageParam = new Page<>(page, size);
        IPage<OrderResponse> responsePage = orderService.getUserOrders(userId, status, pageParam);
        return Result.success(responsePage);
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        OrderResponse response = orderService.getOrderDetail(id, userId);
        return Result.success(response);
    }

    // 取消订单（待付款状态）
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        orderService.cancelOrder(id, userId);
        return Result.success(null);
    }

    // 支付订单
    @PutMapping("/{id}/pay")
    public Result<OrderResponse> payOrder(@PathVariable Long id, @Valid @RequestBody PayRequest request) {
        Long userId = CurrentUser.getId();
        OrderResponse response = orderService.payOrder(id, userId, request.getPassword());
        return Result.success(response);
    }

    // 确认收货
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmReceipt(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        orderService.confirmReceipt(id, userId);
        return Result.success(null);
    }
}