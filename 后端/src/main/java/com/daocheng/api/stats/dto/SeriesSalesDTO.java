package com.daocheng.api.stats.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SeriesSalesDTO {
    private String seriesName;
    private BigDecimal value;       // 销售金额或销量
}