package com.daocheng.api.stats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daocheng.api.order.entity.Order;
import com.daocheng.api.order.entity.OrderItem;
import com.daocheng.api.order.mapper.OrderItemMapper;
import com.daocheng.api.order.mapper.OrderMapper;
import com.daocheng.api.product.entity.Brand;
import com.daocheng.api.product.entity.Product;
import com.daocheng.api.product.entity.Series;
import com.daocheng.api.product.mapper.BrandMapper;
import com.daocheng.api.product.mapper.ProductMapper;
import com.daocheng.api.product.mapper.SeriesMapper;
import com.daocheng.api.stats.dto.*;
import com.daocheng.api.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final SeriesMapper seriesMapper;
    private final BrandMapper brandMapper;

    // 有效订单状态（已支付）
    private static final List<String> VALID_STATUS = Arrays.asList("paid", "shipped", "completed");

    @Override
    public SalesOverviewDTO getOverview() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);

        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = today.atTime(LocalTime.MAX);

        SalesOverviewDTO dto = new SalesOverviewDTO();

        // 今日统计
        dto.setToday(calculatePeriod(startOfToday, endOfToday));

        // 本月统计
        dto.setMonth(calculatePeriod(startOfMonth, endOfMonth));

        // 累计统计
        dto.setTotal(calculatePeriod(null, null));

        return dto;
    }

    private SalesOverviewDTO.Period calculatePeriod(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, VALID_STATUS);
        if (start != null) {
            orderWrapper.ge(Order::getPaidAt, start); // 使用支付时间
        }
        if (end != null) {
            orderWrapper.le(Order::getPaidAt, end);
        }

        List<Order> orders = orderMapper.selectList(orderWrapper);
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        if (orderIds.isEmpty()) {
            SalesOverviewDTO.Period period = new SalesOverviewDTO.Period();
            period.setSalesAmount(BigDecimal.ZERO);
            period.setOrderCount(0);
            period.setSalesVolume(BigDecimal.ZERO);
            return period;
        }

        // 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVolume = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))) // 注意：销量是平方米，直接用quantity
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SalesOverviewDTO.Period period = new SalesOverviewDTO.Period();
        period.setSalesAmount(totalAmount);
        period.setOrderCount(orders.size());
        period.setSalesVolume(totalVolume);
        return period;
    }

    @Override
    public SalesTrendDTO getTrend(LocalDate startDate, LocalDate endDate, String interval) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(6); // 默认最近7天
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        // 查询时间范围内的有效订单
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, VALID_STATUS)
                .ge(Order::getPaidAt, start)
                .le(Order::getPaidAt, end)
                .orderByAsc(Order::getPaidAt);
        List<Order> orders = orderMapper.selectList(orderWrapper);

        // 按日期分组聚合
        Map<LocalDate, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getPaidAt().toLocalDate()));

        List<String> dates = new ArrayList<>();
        List<BigDecimal> salesAmount = new ArrayList<>();
        List<Integer> orderCount = new ArrayList<>();
        List<BigDecimal> salesVolume = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dates.add(current.toString());
            List<Order> dayOrders = ordersByDate.getOrDefault(current, Collections.emptyList());
            orderCount.add(dayOrders.size());

            BigDecimal dayAmount = dayOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            salesAmount.add(dayAmount);

            // 计算当日销量（需关联订单项）
            if (!dayOrders.isEmpty()) {
                List<Long> orderIds = dayOrders.stream().map(Order::getId).collect(Collectors.toList());
                LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
                itemWrapper.in(OrderItem::getOrderId, orderIds);
                List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
                BigDecimal dayVolume = items.stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                salesVolume.add(dayVolume);
            } else {
                salesVolume.add(BigDecimal.ZERO);
            }
            current = current.plusDays(1);
        }

        SalesTrendDTO dto = new SalesTrendDTO();
        dto.setDates(dates);
        dto.setSalesAmount(salesAmount);
        dto.setOrderCount(orderCount);
        dto.setSalesVolume(salesVolume);
        return dto;
    }

    @Override
    public List<SeriesSalesDTO> getSalesBySeries(LocalDate startDate, LocalDate endDate, String type) {
        // 构建时间范围
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        // 查询有效订单的订单项，并关联商品表获取系列ID
        // 由于需要分组，我们使用自定义SQL，或者先查询订单项再在内存中分组（数据量不大时可行）
        // 这里采用先查询订单项，再关联商品和系列的方式

        // 1. 查询有效订单ID
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, VALID_STATUS);
        if (start != null) orderWrapper.ge(Order::getPaidAt, start);
        if (end != null) orderWrapper.le(Order::getPaidAt, end);
        List<Order> orders = orderMapper.selectList(orderWrapper);
        if (orders.isEmpty()) return Collections.emptyList();
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        // 2. 查询订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        if (items.isEmpty()) return Collections.emptyList();

        // 3. 获取商品ID到系列ID的映射
        Set<Long> productIds = items.stream().map(OrderItem::getProductId).collect(Collectors.toSet());
        Map<Long, Long> productSeriesMap = new HashMap<>();
        Map<Long, String> seriesNameMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
            productWrapper.in(Product::getId, productIds);
            List<Product> products = productMapper.selectList(productWrapper);
            for (Product p : products) {
                productSeriesMap.put(p.getId(), p.getSeriesId());
            }
            Set<Long> seriesIds = new HashSet<>(productSeriesMap.values());
            if (!seriesIds.isEmpty()) {
                // 查询系列名称
                LambdaQueryWrapper<Series> seriesWrapper = new LambdaQueryWrapper<>();
                seriesWrapper.in(Series::getId, seriesIds);
                seriesMapper.selectList(seriesWrapper).forEach(s -> seriesNameMap.put(s.getId(), s.getName()));
            }
        }

        // 4. 按系列分组统计
        Map<Long, BigDecimal> seriesValueMap = new HashMap<>();
        for (OrderItem item : items) {
            Long seriesId = productSeriesMap.get(item.getProductId());
            if (seriesId == null) continue;
            BigDecimal value;
            if ("amount".equals(type)) {
                value = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            } else { // volume
                value = BigDecimal.valueOf(item.getQuantity()); // 假设quantity就是平方米数
            }
            seriesValueMap.merge(seriesId, value, BigDecimal::add);
        }

        List<SeriesSalesDTO> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : seriesValueMap.entrySet()) {
            SeriesSalesDTO dto = new SeriesSalesDTO();
            dto.setSeriesName(seriesNameMap.getOrDefault(entry.getKey(), "未知系列"));
            dto.setValue(entry.getValue());
            result.add(dto);
        }

        // 按值降序排序
        result.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return result;
    }

    @Override
    public List<BrandSalesDTO> getSalesByBrand(LocalDate startDate, LocalDate endDate, String type) {
        // 类似系列统计，但需要关联品牌
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, VALID_STATUS);
        if (start != null) orderWrapper.ge(Order::getPaidAt, start);
        if (end != null) orderWrapper.le(Order::getPaidAt, end);
        List<Order> orders = orderMapper.selectList(orderWrapper);
        if (orders.isEmpty()) return Collections.emptyList();
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);
        if (items.isEmpty()) return Collections.emptyList();

        Set<Long> productIds = items.stream().map(OrderItem::getProductId).collect(Collectors.toSet());
        Map<Long, Long> productSeriesMap = new HashMap<>();
        Map<Long, Long> seriesBrandMap = new HashMap<>();
        Map<Long, String> brandNameMap = new HashMap<>();

        if (!productIds.isEmpty()) {
            LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
            productWrapper.in(Product::getId, productIds);
            List<Product> products = productMapper.selectList(productWrapper);
            for (Product p : products) {
                productSeriesMap.put(p.getId(), p.getSeriesId());
            }
            Set<Long> seriesIds = new HashSet<>(productSeriesMap.values());
            if (!seriesIds.isEmpty()) {
                // 查询系列，获取品牌ID
                LambdaQueryWrapper<Series> seriesWrapper = new LambdaQueryWrapper<>();
                seriesWrapper.in(Series::getId, seriesIds);
                seriesMapper.selectList(seriesWrapper).forEach(s -> seriesBrandMap.put(s.getId(), s.getBrandId()));

                Set<Long> brandIds = new HashSet<>(seriesBrandMap.values());
                if (!brandIds.isEmpty()) {
                    LambdaQueryWrapper<Brand> brandWrapper = new LambdaQueryWrapper<>();
                    brandWrapper.in(Brand::getId, brandIds);
                    brandMapper.selectList(brandWrapper).forEach(b -> brandNameMap.put(b.getId(), b.getName()));
                }
            }
        }

        Map<Long, BigDecimal> brandValueMap = new HashMap<>();
        for (OrderItem item : items) {
            Long seriesId = productSeriesMap.get(item.getProductId());
            if (seriesId == null) continue;
            Long brandId = seriesBrandMap.get(seriesId);
            if (brandId == null) continue;
            BigDecimal value;
            if ("amount".equals(type)) {
                value = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            } else {
                value = BigDecimal.valueOf(item.getQuantity());
            }
            brandValueMap.merge(brandId, value, BigDecimal::add);
        }

        List<BrandSalesDTO> result = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : brandValueMap.entrySet()) {
            BrandSalesDTO dto = new BrandSalesDTO();
            dto.setBrandName(brandNameMap.getOrDefault(entry.getKey(), "未知品牌"));
            dto.setValue(entry.getValue());
            result.add(dto);
        }
        result.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return result;
    }

    @Override
    public List<TopProductDTO> getTopProducts(int limit, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(Order::getStatus, VALID_STATUS);
        if (start != null) orderWrapper.ge(Order::getPaidAt, start);
        if (end != null) orderWrapper.le(Order::getPaidAt, end);
        List<Order> orders = orderMapper.selectList(orderWrapper);
        if (orders.isEmpty()) return Collections.emptyList();
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        // 按商品ID分组统计
        Map<Long, TopProductAgg> aggMap = new HashMap<>();
        for (OrderItem item : items) {
            TopProductAgg agg = aggMap.computeIfAbsent(item.getProductId(), k -> new TopProductAgg());
            agg.productId = item.getProductId();
            agg.productName = item.getProductName(); // 使用订单快照名称
            agg.salesAmount = agg.salesAmount.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            agg.salesVolume = agg.salesVolume.add(BigDecimal.valueOf(item.getQuantity()));
        }

        List<TopProductDTO> result = aggMap.values().stream()
                .map(agg -> {
                    TopProductDTO dto = new TopProductDTO();
                    dto.setProductId(agg.productId);
                    dto.setProductName(agg.productName);
                    dto.setSalesAmount(agg.salesAmount);
                    dto.setSalesVolume(agg.salesVolume);
                    return dto;
                })
                .sorted((a, b) -> b.getSalesAmount().compareTo(a.getSalesAmount()))
                .limit(limit)
                .collect(Collectors.toList());

        return result;
    }

    // 辅助内部类
    private static class TopProductAgg {
        Long productId;
        String productName;
        BigDecimal salesAmount = BigDecimal.ZERO;
        BigDecimal salesVolume = BigDecimal.ZERO;
    }
}