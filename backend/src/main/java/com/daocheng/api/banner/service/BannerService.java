package com.daocheng.api.banner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.banner.dto.request.BannerRequest;
import com.daocheng.api.banner.dto.response.BannerResponse;
import com.daocheng.api.banner.entity.Banner;

import java.util.List;

public interface BannerService extends IService<Banner> {
    BannerResponse addBanner(BannerRequest request);
    BannerResponse updateBanner(Long id, BannerRequest request);
    void updateStatus(Long id, Boolean enabled);
    void deleteBanner(Long id);
    IPage<BannerResponse> pageBanners(Page<Banner> page, Boolean enabled);
    List<BannerResponse> getEnabledBanners(); // 用于首页
}