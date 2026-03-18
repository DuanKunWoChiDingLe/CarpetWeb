package com.daocheng.api.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.product.dto.request.SeriesRequest;
import com.daocheng.api.product.dto.response.SeriesResponse;
import com.daocheng.api.product.entity.Series;
import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;

public interface SeriesService extends IService<Series> {
    SeriesResponse addSeries(SeriesRequest request);
    SeriesResponse updateSeries(Long id, SeriesRequest request);
    void deleteSeries(Long id);
    IPage<SeriesResponse> pageSeries(Page<Series> page, Long brandId, LayTypeEnum layType, MaterialEnum material, String keyword);
    SeriesResponse getSeriesDetail(Long id);
}