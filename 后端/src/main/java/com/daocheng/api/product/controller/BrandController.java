package com.daocheng.api.product.controller;

import com.daocheng.api.common.dto.Result;
import com.daocheng.api.product.dto.request.BrandRequest;
import com.daocheng.api.product.dto.response.BrandResponse;
import com.daocheng.api.product.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public Result<List<BrandResponse>> getAllBrands() {
        return Result.success(brandService.getAllBrands());
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping
    public Result<BrandResponse> addBrand(@Valid @RequestBody BrandRequest request) {
        return Result.success(brandService.addBrand(request));
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PutMapping("/{id}")
    public Result<BrandResponse> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequest request) {
        return Result.success(brandService.updateBrand(id, request));
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return Result.success(null);
    }
}
