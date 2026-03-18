package com.daocheng.api.banner.controller;

import com.daocheng.api.banner.dto.response.BannerResponse;
import com.daocheng.api.banner.service.BannerService;
import com.daocheng.api.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/banners")
@RequiredArgsConstructor
public class PublicBannerController {

    private final BannerService bannerService;

    @GetMapping
    public Result<List<BannerResponse>> getEnabledBanners() {
        return Result.success(bannerService.getEnabledBanners());
    }
}