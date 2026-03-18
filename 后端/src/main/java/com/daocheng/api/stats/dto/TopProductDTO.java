package com.daocheng.api.stats.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopProductDTO {
    private Long productId;
    private String productName;
    private BigDecimal salesAmount;
    private BigDecimal salesVolume;
}