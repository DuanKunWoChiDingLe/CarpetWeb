package com.daocheng.api.cart.controller;

import com.daocheng.api.cart.dto.CartItemDTO;
import com.daocheng.api.cart.dto.AddToCartRequest;
import com.daocheng.api.cart.dto.UpdateCartItemRequest;
import com.daocheng.api.cart.service.CartService;
import com.daocheng.api.common.dto.Result;
import com.daocheng.api.common.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CartItemDTO>> getCart() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(cartService.getCartItems(userId));
    }

    @PostMapping
    public Result<Void> addToCart(@Valid @RequestBody AddToCartRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.addToCart(userId, request);
        return Result.success(null);
    }

    @PutMapping("/{cartItemId}")
    public Result<Void> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.updateCartItem(userId, cartItemId, request);
        return Result.success(null);
    }

    @DeleteMapping("/{cartItemId}")
    public Result<Void> removeCartItem(@PathVariable Long cartItemId) {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.removeCartItem(userId, cartItemId);
        return Result.success(null);
    }

    @DeleteMapping("/clear")
    public Result<Void> clearCart() {
        Long userId = SecurityUtils.getCurrentUserId();
        cartService.clearCart(userId);
        return Result.success(null);
    }
}