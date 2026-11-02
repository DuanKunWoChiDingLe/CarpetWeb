package com.daocheng.api.stats.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BrandSalesDTO {
    private String brandName;
    private BigDecimal value;
}