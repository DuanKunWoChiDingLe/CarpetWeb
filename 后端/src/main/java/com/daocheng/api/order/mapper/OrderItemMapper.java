package com.daocheng.api.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daocheng.api.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单商品明细 Mapper 接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    // 如需复杂查询，可在此添加方法
}