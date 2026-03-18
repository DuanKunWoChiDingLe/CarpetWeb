package com.daocheng.api.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daocheng.api.common.dto.Result;
import com.daocheng.api.product.dto.request.ProductRequest;
import com.daocheng.api.product.dto.response.ProductResponse;
import com.daocheng.api.product.entity.Product;
import com.daocheng.api.product.service.ProductService;
import com.daocheng.api.product.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SeriesService seriesService;  // 注意注入

    @GetMapping("/hot")
    public Result<List<ProductResponse>> getHotProducts(@RequestParam(defaultValue = "4") int limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getSales)
                .orderByDesc(Product::getCreatedAt)
                .last("LIMIT " + limit);
        List<Product> products = productService.list(wrapper);
        List<ProductResponse> responses = products.stream()
                .map(productService::convertToResponse)
                .collect(Collectors.toList());
        return Result.success(responses);
    }

    @GetMapping("/latest")
    public Result<IPage<ProductResponse>> getLatestProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Product::getCreatedAt);
        IPage<Product> productPage = productService.page(pageParam, wrapper);
        IPage<ProductResponse> responsePage = productPage.convert(product ->
                productService.convertToResponse(product)
        );
        return Result.success(responsePage);
    }


    @GetMapping("/series/{seriesId}")
    public Result<IPage<ProductResponse>> pageProductsBySeries(
            @PathVariable Long seriesId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "default") String sort) {
        Page<Product> pageParam = new Page<>(page, size);
        return Result.success(productService.pageProductsBySeries(pageParam, seriesId, sort));
    }

    @GetMapping("/{id}")
    public Result<ProductResponse> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @PostMapping
    public Result<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        return Result.success(productService.addProduct(request));
    }

    @PutMapping("/{id}")
    public Result<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return Result.success(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success(null);
    }

    @GetMapping("/search")
    public Result<IPage<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Product::getName, keyword)
                .orderByDesc(Product::getCreatedAt);
        IPage<Product> productPage = productService.page(pageParam, wrapper);
        IPage<ProductResponse> responsePage = productPage.convert(product ->
                productService.convertToResponse(product));
        return Result.success(responsePage);
    }

    // 批量上下架接口等可后续添加
}