package com.daocheng.api.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.product.dto.request.ProductRequest;
import com.daocheng.api.product.dto.response.ProductResponse;
import com.daocheng.api.product.entity.Product;

public interface ProductService extends IService<Product> {
    ProductResponse addProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    IPage<ProductResponse> pageProductsBySeries(Page<Product> page, Long seriesId, String sort);
    ProductResponse getProductDetail(Long id);
    ProductResponse convertToResponse(Product product);
}