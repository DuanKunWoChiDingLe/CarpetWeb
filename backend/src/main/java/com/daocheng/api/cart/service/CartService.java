package com.daocheng.api.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daocheng.api.cart.dto.CartItemDTO;
import com.daocheng.api.cart.dto.AddToCartRequest;
import com.daocheng.api.cart.dto.UpdateCartItemRequest;
import com.daocheng.api.cart.entity.Cart;
import java.util.List;

public interface CartService extends IService<Cart> {
    // 获取当前用户的购物车（所有商品项）
    List<CartItemDTO> getCartItems(Long userId);

    // 添加商品到购物车
    void addToCart(Long userId, AddToCartRequest request);

    // 更新购物车中商品数量
    void updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request);

    // 删除购物车中的商品项
    void removeCartItem(Long userId, Long cartItemId);

    // 清空购物车
    void clearCart(Long userId);
}