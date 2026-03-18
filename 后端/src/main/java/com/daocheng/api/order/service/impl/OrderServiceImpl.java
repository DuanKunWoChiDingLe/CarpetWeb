package com.daocheng.api.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.common.exception.BusinessException;
import com.daocheng.api.order.dto.request.OrderCreateRequest;
import com.daocheng.api.order.dto.request.CartItem;
import com.daocheng.api.order.dto.response.OrderItemResponse;
import com.daocheng.api.order.dto.response.OrderResponse;
import com.daocheng.api.order.entity.Order;
import com.daocheng.api.order.entity.OrderItem;
import com.daocheng.api.order.mapper.OrderItemMapper;
import com.daocheng.api.order.mapper.OrderMapper;
import com.daocheng.api.order.service.OrderService;
import com.daocheng.api.product.entity.Product;
import com.daocheng.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final ProductService productService;
    private final SimpMessagingTemplate messagingTemplate; // 新增
    // 如果存在购物车服务，可注入用于清空购物车等操作
    // private final CartService cartService;

    @Value("${order.pay-password:010509}")
    private String payPassword;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(Long userId, String userName, OrderCreateRequest request) {
        // 1. 参数校验
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(400, "商品列表不能为空");
        }

        // 2. 生成订单号
        String orderNo = generateOrderNo();

        // 3. 计算总金额并扣减库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : request.getItems()) {
            Product product = productService.getById(item.getProductId());
            if (product == null) {
                throw new BusinessException(400, "商品不存在，ID: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException(400, "商品库存不足：" + product.getName());
            }

            // 扣减库存
            product.setStock(product.getStock() - item.getQuantity());
            productService.updateById(product);

            // 计算小计
            BigDecimal subtotal = product.getPricePerSqm().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // 构建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            // 取第一张图片作为快照
            String firstImage = extractFirstImage(product.getImages());
            orderItem.setProductImage(firstImage);
            orderItem.setPrice(product.getPricePerSqm());
            orderItem.setQuantity(item.getQuantity());
            orderItems.add(orderItem);
        }

        // 4. 保存订单主表
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setUserName(userName);
        order.setTotalAmount(totalAmount);
        order.setStatus("pending");
        order.setConsignee(request.getConsignee());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setRemark(request.getRemark());
        save(order);

        // 5. 保存订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }

        // 6. 清空购物车中已下单的商品（可选）
        // List<Long> productIds = request.getItems().stream().map(CartItem::getProductId).collect(Collectors.toList());
        // cartService.removeItems(userId, productIds);
        log.info("准备保存的订单对象：userId={}, orderNo={}, ...", order.getUserId(), order.getOrderNo());
        // 7. 返回响应
        return convertToResponse(order, orderItems);

    }

    @Override
    public IPage<OrderResponse> getUserOrders(Long userId, String status, Page<Order> page) {
        // 1. 查询订单主表
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt); // 按下单时间倒序
        IPage<Order> orderPage = baseMapper.selectPage(page, wrapper);

        // 2. 如果无订单，直接返回空页
        if (orderPage.getRecords().isEmpty()) {
            Page<OrderResponse> emptyPage = new Page<>(page.getCurrent(), page.getSize(), 0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }

        // 3. 获取订单ID列表并批量查询订单项
        List<Long> orderIds = orderPage.getRecords().stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

        // 4. 按订单ID分组
        Map<Long, List<OrderItem>> itemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        // 5. 组装响应
        List<OrderResponse> records = orderPage.getRecords().stream()
                .map(order -> convertToResponse(order, itemMap.getOrDefault(order.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        Page<OrderResponse> responsePage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId, Long userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        // 用户端查看需校验用户ID（userId不为空时校验）
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看此订单");
        }

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        return convertToResponse(order, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待付款订单可以取消");
        }

        // 恢复库存
        restoreStock(orderId);

        // 更新订单状态
        order.setStatus("cancelled");
        updateById(order);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse payOrder(Long orderId, Long userId, String password) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法支付");
        }
        if (!payPassword.equals(password)) {
            throw new BusinessException(400, "支付密码错误");
        }

        order.setStatus("paid");
        order.setPaidAt(LocalDateTime.now());
        updateById(order);

        // 支付成功后，统计待处理订单数量（例如已付款待发货的订单数）
        int pendingOrderCount = countPendingOrders(); // 统计方法
        messagingTemplate.convertAndSend("/topic/newOrders", pendingOrderCount); // 广播给所有商家

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        return convertToResponse(order, items);
    }

    /**
     * 统计待处理订单数量（可根据业务定义，这里以 "paid" 为例）
     */
    private int countPendingOrders() {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, "paid"); // 待发货状态
        return Math.toIntExact(baseMapper.selectCount(wrapper));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Long orderId, Long userId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (!"shipped".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已发货订单可以确认收货");
        }
        order.setStatus("completed");
        updateById(order);
    }

    // ---------- 商家端 ----------

    @Override
    public IPage<OrderResponse> getAllOrders(String status, String keyword, String startDate, String endDate, Page<Order> page) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or().like(Order::getUserName, keyword));
        }
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            wrapper.between(Order::getCreatedAt, startDate + " 00:00:00", endDate + " 23:59:59");
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        IPage<Order> orderPage = baseMapper.selectPage(page, wrapper);

        if (orderPage.getRecords().isEmpty()) {
            Page<OrderResponse> emptyPage = new Page<>(page.getCurrent(), page.getSize(), 0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }

        List<Long> orderIds = orderPage.getRecords().stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

        Map<Long, List<OrderItem>> itemMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderResponse> records = orderPage.getRecords().stream()
                .map(order -> convertToResponse(order, itemMap.getOrDefault(order.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        Page<OrderResponse> responsePage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已付款订单可以发货");
        }
        order.setStatus("shipped");
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void merchantCancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        // 商家可取消的订单状态：待付款、已付款？根据业务决定，这里只允许取消待付款
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待付款订单可以取消");
        }
        // 恢复库存
        restoreStock(orderId);

        order.setStatus("cancelled");
        updateById(order);
    }

    // ---------- 辅助方法 ----------

    private String generateOrderNo() {
        // 生成格式：yyyyMMddHHmmss + 4位随机数
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(10000);
        return datePart + String.format("%04d", random);
    }

    /**
     * 从图片JSON字符串中提取第一张图片路径
     */
    private String extractFirstImage(String imagesJson) {
        if (!StringUtils.hasText(imagesJson) || "[]".equals(imagesJson)) {
            return null;
        }
        // 简单处理：去掉首尾的[]和引号，取第一个
        try {
            String trimmed = imagesJson.substring(1, imagesJson.length() - 1);
            if (trimmed.isEmpty()) {
                return null;
            }
            String[] parts = trimmed.split(",");
            String first = parts[0].trim();
            return first.replace("\"", "");
        } catch (Exception e) {
            log.error("解析图片JSON失败: {}", imagesJson, e);
            return null;
        }
    }

    /**
     * 恢复订单库存（取消订单时使用）
     */
    private void restoreStock(Long orderId) {
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        for (OrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productService.updateById(product);
            }
        }
    }

    /**
     * 将订单和订单项转换为响应对象
     */
    private OrderResponse convertToResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses == null ? Collections.emptyList() : itemResponses);
        return response;
    }

    private OrderItemResponse convertItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        BeanUtils.copyProperties(item, response);
        return response;
    }
}