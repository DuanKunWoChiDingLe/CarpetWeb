package com.daocheng.api.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daocheng.api.common.dto.Result;
import com.daocheng.api.order.dto.response.OrderResponse;
import com.daocheng.api.order.entity.Order;
import com.daocheng.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/orders")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderService orderService;

    // 分页查询所有订单（支持状态、日期筛选）
    @GetMapping
    public Result<IPage<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<Order> pageParam = new Page<>(page, size);
        IPage<OrderResponse> responsePage = orderService.getAllOrders(status, keyword, startDate, endDate, pageParam);
        return Result.success(responsePage);
    }

    // 获取订单详情
    @GetMapping("/{id}")
    public Result<OrderResponse> getOrderDetail(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderDetail(id, null); // 商家查看无需用户ID校验
        return Result.success(response);
    }

    // 发货
    @PutMapping("/{id}/ship")
    public Result<Void> shipOrder(@PathVariable Long id) {
        orderService.shipOrder(id);
        return Result.success(null);
    }

    // 商家取消订单（可能有限制）
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.merchantCancelOrder(id);
        return Result.success(null);
    }
}