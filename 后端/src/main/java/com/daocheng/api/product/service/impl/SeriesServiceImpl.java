package com.daocheng.api.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.common.exception.BusinessException;
import com.daocheng.api.product.dto.request.SeriesRequest;
import com.daocheng.api.product.dto.response.SeriesResponse;
import com.daocheng.api.product.entity.Brand;
import com.daocheng.api.product.entity.Product;
import com.daocheng.api.product.entity.Series;
import com.daocheng.api.product.enums.LayTypeEnum;
import com.daocheng.api.product.enums.MaterialEnum;
import com.daocheng.api.product.mapper.ProductMapper;
import com.daocheng.api.product.mapper.SeriesMapper;
import com.daocheng.api.product.service.BrandService;
import com.daocheng.api.product.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SeriesServiceImpl extends ServiceImpl<SeriesMapper, Series> implements SeriesService {

    private final BrandService brandService;
    private final ProductMapper productMapper;  // 改为注入 Mapper，而不是 ProductService

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeriesResponse addSeries(SeriesRequest request) {
        // 检查品牌是否存在
        Brand brand = brandService.getById(request.getBrandId());
        if (brand == null) {
            throw new BusinessException(400, "品牌不存在");
        }

        // 检查同品牌下系列名是否重复
        LambdaQueryWrapper<Series> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Series::getBrandId, request.getBrandId())
                .eq(Series::getName, request.getName());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "该品牌下已存在同名系列");
        }

        Series series = new Series();
        BeanUtils.copyProperties(request, series);
        save(series);
        return convertToResponse(series, brand.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeriesResponse updateSeries(Long id, SeriesRequest request) {
        Series series = getById(id);
        if (series == null) {
            throw new BusinessException(404, "系列不存在");
        }

        // 如果修改了品牌或系列名，检查是否冲突
        if (!series.getBrandId().equals(request.getBrandId()) || !series.getName().equals(request.getName())) {
            Brand brand = brandService.getById(request.getBrandId());
            if (brand == null) {
                throw new BusinessException(400, "品牌不存在");
            }
            LambdaQueryWrapper<Series> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Series::getBrandId, request.getBrandId())
                    .eq(Series::getName, request.getName());
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "该品牌下已存在同名系列");
            }
        }

        BeanUtils.copyProperties(request, series);
        updateById(series);

        Brand brand = brandService.getById(series.getBrandId());
        return convertToResponse(series, brand != null ? brand.getName() : null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSeries(Long id) {
        // 检查该系列下是否有商品，若有则禁止删除
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSeriesId, id);
        Long productCount = productMapper.selectCount(wrapper);
        if (productCount != null && productCount > 0) {
            throw new BusinessException(400, "该系列下存在商品，无法删除");
        }
        removeById(id);
    }

    @Override
    public IPage<SeriesResponse> pageSeries(Page<Series> page, Long brandId, LayTypeEnum layType, MaterialEnum material, String keyword) {
        LambdaQueryWrapper<Series> wrapper = new LambdaQueryWrapper<>();
        if (brandId != null) {
            wrapper.eq(Series::getBrandId, brandId);
        }
        if (layType != null) {
            wrapper.eq(Series::getLayType, layType);
        }
        if (material != null) {
            wrapper.eq(Series::getMaterial, material);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Series::getName, keyword);
        }
        wrapper.orderByDesc(Series::getCreatedAt); // 默认按创建时间倒序

        IPage<Series> seriesPage = page(page, wrapper);
        return seriesPage.convert(series -> {
            Brand brand = brandService.getById(series.getBrandId());
            return convertToResponse(series, brand != null ? brand.getName() : null);
        });
    }

    @Override
    public SeriesResponse getSeriesDetail(Long id) {
        Series series = getById(id);
        if (series == null) {
            throw new BusinessException(404, "系列不存在");
        }
        Brand brand = brandService.getById(series.getBrandId());
        return convertToResponse(series, brand != null ? brand.getName() : null);
    }

    private SeriesResponse convertToResponse(Series series, String brandName) {
        SeriesResponse response = new SeriesResponse();
        BeanUtils.copyProperties(series, response);
        response.setBrandId(series.getBrandId());
        response.setBrandName(brandName);

        // 统计系列下的色号数量（直接使用 Mapper 查询）
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSeriesId, series.getId());
        Long colorCount = productMapper.selectCount(wrapper);
        response.setColorCount(colorCount != null ? colorCount.intValue() : 0);
        return response;
    }
}