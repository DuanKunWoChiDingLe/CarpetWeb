package com.daocheng.api.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daocheng.api.common.dto.Result;
import com.daocheng.api.product.dto.request.SeriesRequest;
import com.daocheng.api.product.dto.response.SeriesResponse;
import com.daocheng.api.product.entity.Series;
import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;
import com.daocheng.api.product.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @GetMapping
    public Result<IPage<SeriesResponse>> pageSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) LayTypeEnum layType,
            @RequestParam(required = false) MaterialEnum material,
            @RequestParam(required = false) String keyword) {
        Page<Series> pageParam = new Page<>(page, size);
        return Result.success(seriesService.pageSeries(pageParam, brandId, layType, material, keyword));
    }

    @GetMapping("/{id}")
    public Result<SeriesResponse> getSeriesDetail(@PathVariable Long id) {
        return Result.success(seriesService.getSeriesDetail(id));
    }

    @PostMapping
    public Result<SeriesResponse> addSeries(@Valid @RequestBody SeriesRequest request) {
        return Result.success(seriesService.addSeries(request));
    }

    @PutMapping("/{id}")
    public Result<SeriesResponse> updateSeries(@PathVariable Long id, @Valid @RequestBody SeriesRequest request) {
        return Result.success(seriesService.updateSeries(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return Result.success(null);
    }
}