package com.daocheng.api.banner.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daocheng.api.banner.dto.request.BannerRequest;
import com.daocheng.api.banner.dto.response.BannerResponse;
import com.daocheng.api.banner.dto.request.BannerStatusRequest;
import com.daocheng.api.banner.entity.Banner;
import com.daocheng.api.banner.service.BannerService;
import com.daocheng.api.common.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('merchant')") // 需要商家权限
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public Result<IPage<BannerResponse>> pageBanners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean enabled) {
        Page<Banner> pageParam = new Page<>(page, size);
        return Result.success(bannerService.pageBanners(pageParam, enabled));
    }

    @PostMapping
    public Result<BannerResponse> addBanner(@Valid @RequestBody BannerRequest request) {
        return Result.success(bannerService.addBanner(request));
    }

    @PutMapping("/{id}")
    public Result<BannerResponse> updateBanner(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        return Result.success(bannerService.updateBanner(id, request));
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody BannerStatusRequest request) {
        bannerService.updateStatus(id, request.getEnabled());
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.success(null);
    }
}