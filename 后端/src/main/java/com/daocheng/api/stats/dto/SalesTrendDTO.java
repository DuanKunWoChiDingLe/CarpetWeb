package com.daocheng.api.stats.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesTrendDTO {
    private List<String> dates;
    private List<BigDecimal> salesAmount;
    private List<Integer> orderCount;
    private List<BigDecimal> salesVolume;
}