package com.daocheng.api.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.common.exception.BusinessException;
import com.daocheng.api.product.dto.request.ProductRequest;
import com.daocheng.api.product.dto.response.ProductResponse;
import com.daocheng.api.product.entity.Brand;
import com.daocheng.api.product.entity.Product;
import com.daocheng.api.product.entity.Series;
import com.daocheng.api.product.mapper.ProductMapper;
import com.daocheng.api.product.service.BrandService;
import com.daocheng.api.product.service.ProductService;
import com.daocheng.api.product.service.SeriesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final SeriesService seriesService;
    private final BrandService brandService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse addProduct(ProductRequest request) {
        Series series = seriesService.getById(request.getSeriesId());
        if (series == null) {
            throw new BusinessException(400, "所属系列不存在");
        }

        // 检查同系列下色号是否重复
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSeriesId, request.getSeriesId())
                .eq(Product::getColorCode, request.getColorCode());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "该系列下已存在相同色号");
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        // 处理商品名称：如果前端未提供，则自动生成
        if (!StringUtils.hasText(request.getName())) {
            product.setName(series.getName() + request.getColorCode());
        }

        // 处理图片列表转JSON字符串
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                product.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                throw new BusinessException(500, "图片格式处理失败");
            }
        } else {
            product.setImages("[]");
        }

        // 默认状态为上架（如果前端未传）
        if (request.getStatus() == null) {
            product.setStatus(1);
        }

        save(product);
        return convertToResponse(product, series);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        Series series = seriesService.getById(request.getSeriesId());
        if (series == null) {
            throw new BusinessException(400, "所属系列不存在");
        }

        // 如果修改了系列或色号，检查是否与其他商品冲突
        if (!Objects.equals(product.getSeriesId(), request.getSeriesId()) ||
                !product.getColorCode().equals(request.getColorCode())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getSeriesId, request.getSeriesId())
                    .eq(Product::getColorCode, request.getColorCode());
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "该系列下已存在相同色号");
            }
        }

        BeanUtils.copyProperties(request, product);

        // 处理商品名称
        if (!StringUtils.hasText(request.getName())) {
            product.setName(series.getName() + request.getColorCode());
        } else {
            product.setName(request.getName());
        }

        // 处理图片
        if (request.getImages() != null) {
            try {
                product.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                throw new BusinessException(500, "图片格式处理失败");
            }
        } else {
            product.setImages("[]");
        }

        updateById(product);
        return convertToResponse(product, series);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        removeById(id);
    }

    @Override
    public IPage<ProductResponse> pageProductsBySeries(Page<Product> page, Long seriesId, String sort) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSeriesId, seriesId);

        // 处理排序
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getPricePerSqm);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(Product::getPricePerSqm);
        } else if ("name_asc".equals(sort)) {
            wrapper.orderByAsc(Product::getName);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(Product::getCreatedAt);
        }

        IPage<Product> productPage = page(page, wrapper);
        return productPage.convert(product -> {
            Series series = seriesService.getById(product.getSeriesId());
            return convertToResponse(product, series);
        });
    }

    @Override
    public ProductResponse getProductDetail(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        Series series = seriesService.getById(product.getSeriesId());
        return convertToResponse(product, series);
    }

    private ProductResponse convertToResponse(Product product, Series series) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        response.setSeriesId(series.getId());
        response.setSeriesName(series.getName());

        // 获取品牌名称
        Brand brand = brandService.getById(series.getBrandId());
        response.setBrandName(brand != null ? brand.getName() : null);

        response.setLayType(series.getLayType());
        response.setMaterial(series.getMaterial());
        response.setSpec(series.getSpec());

        // 解析图片JSON
        if (StringUtils.hasText(product.getImages())) {
            try {
                List<String> images = objectMapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                response.setImages(images);
            } catch (JsonProcessingException e) {
                response.setImages(new ArrayList<>());
            }
        } else {
            response.setImages(new ArrayList<>());
        }

        return response;
    }

    @Override
    public ProductResponse convertToResponse(Product product) {
        Series series = seriesService.getById(product.getSeriesId());
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        response.setSeriesId(series.getId());
        response.setSeriesName(series.getName());

        Brand brand = brandService.getById(series.getBrandId());
        response.setBrandName(brand != null ? brand.getName() : null);

        response.setLayType(series.getLayType());
        response.setMaterial(series.getMaterial());
        response.setSpec(series.getSpec());

        // 解析图片 JSON
        if (StringUtils.hasText(product.getImages())) {
            try {
                List<String> images = objectMapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                response.setImages(images);
            } catch (JsonProcessingException e) {
                response.setImages(new ArrayList<>());
            }
        } else {
            response.setImages(new ArrayList<>());
        }

        return response;
    }
}