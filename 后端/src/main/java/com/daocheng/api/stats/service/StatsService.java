package com.daocheng.api.stats.service;

import com.daocheng.api.stats.dto.*;
import java.time.LocalDate;
import java.util.List;

public interface StatsService {
    SalesOverviewDTO getOverview();
    SalesTrendDTO getTrend(LocalDate startDate, LocalDate endDate, String interval);
    List<SeriesSalesDTO> getSalesBySeries(LocalDate startDate, LocalDate endDate, String type);
    List<BrandSalesDTO> getSalesByBrand(LocalDate startDate, LocalDate endDate, String type);
    List<TopProductDTO> getTopProducts(int limit, LocalDate startDate, LocalDate endDate);
}