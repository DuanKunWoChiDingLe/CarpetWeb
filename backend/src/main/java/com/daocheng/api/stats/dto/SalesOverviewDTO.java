package com.daocheng.api.stats.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesOverviewDTO {
    private Period today;
    private Period month;
    private Period total;

    @Data
    public static class Period {
        private BigDecimal salesAmount;   // 销售金额
        private Integer orderCount;       // 订单数
        private BigDecimal salesVolume;   // 销量（平方米）
    }
}