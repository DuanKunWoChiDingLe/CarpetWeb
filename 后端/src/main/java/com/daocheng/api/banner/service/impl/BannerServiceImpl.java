package com.daocheng.api.banner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.banner.dto.request.BannerRequest;
import com.daocheng.api.banner.dto.response.BannerResponse;
import com.daocheng.api.banner.entity.Banner;
import com.daocheng.api.banner.mapper.BannerMapper;
import com.daocheng.api.banner.service.BannerService;
import com.daocheng.api.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    @Transactional
    public BannerResponse addBanner(BannerRequest request) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(request, banner);
        if (banner.getEnabled() == null) {
            banner.setEnabled(true);
        }
        save(banner);
        return convertToResponse(banner);
    }

    @Override
    @Transactional
    public BannerResponse updateBanner(Long id, BannerRequest request) {
        Banner banner = getById(id);
        if (banner == null) {
            throw new BusinessException(404, "轮播图不存在");
        }
        BeanUtils.copyProperties(request, banner);
        updateById(banner);
        return convertToResponse(banner);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Boolean enabled) {
        Banner banner = getById(id);
        if (banner == null) {
            throw new BusinessException(404, "轮播图不存在");
        }
        banner.setEnabled(enabled);
        updateById(banner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        if (!removeById(id)) {
            throw new BusinessException(404, "轮播图不存在");
        }
    }

    @Override
    public IPage<BannerResponse> pageBanners(Page<Banner> page, Boolean enabled) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        if (enabled != null) {
            wrapper.eq(Banner::getEnabled, enabled);
        }
        wrapper.orderByAsc(Banner::getSortOrder);
        IPage<Banner> bannerPage = page(page, wrapper);
        return bannerPage.convert(this::convertToResponse);
    }

    @Override
    public List<BannerResponse> getEnabledBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getEnabled, true)
                .orderByAsc(Banner::getSortOrder);
        List<Banner> banners = list(wrapper);
        return banners.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private BannerResponse convertToResponse(Banner banner) {
        BannerResponse response = new BannerResponse();
        BeanUtils.copyProperties(banner, response);
        return response;
    }
}