package com.daocheng.api.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.common.exception.BusinessException;
import com.daocheng.api.product.dto.request.BrandRequest;
import com.daocheng.api.product.dto.response.BrandResponse;
import com.daocheng.api.product.entity.Brand;
import com.daocheng.api.product.mapper.BrandMapper;
import com.daocheng.api.product.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BrandResponse addBrand(BrandRequest request) {
        // 检查品牌名是否已存在
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Brand::getName, request.getName());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "品牌名称已存在");
        }

        Brand brand = new Brand();
        BeanUtils.copyProperties(request, brand);
        save(brand);
        return convertToResponse(brand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = getById(id);
        if (brand == null) {
            throw new BusinessException(404, "品牌不存在");
        }

        // 如果修改了名称，检查新名称是否与其他品牌冲突
        if (!brand.getName().equals(request.getName())) {
            LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Brand::getName, request.getName());
            if (baseMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "品牌名称已存在");
            }
        }

        BeanUtils.copyProperties(request, brand);
        updateById(brand);
        return convertToResponse(brand);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(Long id) {
        // 可在此检查品牌下是否有系列，若有则禁止删除
        removeById(id);
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        List<Brand> brands = list();
        return brands.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BrandResponse convertToResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        BeanUtils.copyProperties(brand, response);
        return response;
    }
}