package com.daocheng.api.stats.controller;

import com.daocheng.api.common.dto.Result;
import com.daocheng.api.stats.dto.*;
import com.daocheng.api.stats.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats/sales")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/overview")
    public Result<SalesOverviewDTO> getOverview() {
        return Result.success(statsService.getOverview());
    }

    @GetMapping("/trend")
    public Result<SalesTrendDTO> getTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "day") String interval) {
        return Result.success(statsService.getTrend(startDate, endDate, interval));
    }

    @GetMapping("/by-series")
    public Result<List<SeriesSalesDTO>> getSalesBySeries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "amount") String type) {
        return Result.success(statsService.getSalesBySeries(startDate, endDate, type));
    }

    @GetMapping("/by-brand")
    public Result<List<BrandSalesDTO>> getSalesByBrand(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "amount") String type) {
        return Result.success(statsService.getSalesByBrand(startDate, endDate, type));
    }

    @GetMapping("/top-products")
    public Result<List<TopProductDTO>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getTopProducts(limit, startDate, endDate));
    }
}