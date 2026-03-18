package com.daocheng.api.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.product.dto.request.BrandRequest;
import com.daocheng.api.product.dto.response.BrandResponse;
import com.daocheng.api.product.entity.Brand;

import java.util.List;

public interface BrandService extends IService<Brand> {
    BrandResponse addBrand(BrandRequest request);
    BrandResponse updateBrand(Long id, BrandRequest request);
    void deleteBrand(Long id);
    List<BrandResponse> getAllBrands();
}