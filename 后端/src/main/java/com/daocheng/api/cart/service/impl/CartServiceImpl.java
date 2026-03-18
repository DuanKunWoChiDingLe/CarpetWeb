package com.daocheng.api.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daocheng.api.cart.dto.CartItemDTO;
import com.daocheng.api.cart.dto.AddToCartRequest;
import com.daocheng.api.cart.dto.UpdateCartItemRequest;
import com.daocheng.api.cart.entity.Cart;
import com.daocheng.api.cart.entity.CartItem;
import com.daocheng.api.cart.mapper.CartItemMapper;
import com.daocheng.api.cart.mapper.CartMapper;
import com.daocheng.api.cart.service.CartService;
import com.daocheng.api.common.exception.BusinessException;
import com.daocheng.api.product.entity.Product;
import com.daocheng.api.product.mapper.ProductMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    // 获取或创建用户的购物车（返回 cartId）
    private Long getOrCreateCartId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        Cart cart = getOne(wrapper);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            save(cart);
        }
        return cart.getId();
    }

    @Override
    public List<CartItemDTO> getCartItems(Long userId) {
        Long cartId = getOrCreateCartId(userId);
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getCartId, cartId);
        List<CartItem> items = cartItemMapper.selectList(wrapper);
        return items.stream().map(cartItem -> {
            CartItemDTO dto = new CartItemDTO();
            dto.setId(cartItem.getId());
            dto.setProductId(cartItem.getProductId());
            dto.setQuantity(cartItem.getQuantity());
            // 查询商品当前信息（名称、价格、图片）
            Product product = productMapper.selectById(cartItem.getProductId());
            if (product != null) {
                dto.setName(product.getName());
                dto.setPrice(product.getPricePerSqm());
                // 处理图片 JSON 数组，取第一张图片
                String imagesJson = product.getImages();
                if (imagesJson != null && !imagesJson.isEmpty()) {
                    try {
                        List<String> imagesList = objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
                        if (!imagesList.isEmpty()) {
                            dto.setImage(imagesList.get(0));
                        }
                    } catch (Exception e) {
                        // 解析失败则忽略，保留空
                    }
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToCart(Long userId, AddToCartRequest request) {
        Long cartId = getOrCreateCartId(userId);
        // 检查商品是否存在且库存足够
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        // 检查是否已存在
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getCartId, cartId)
                .eq(CartItem::getProductId, request.getProductId());
        CartItem existing = cartItemMapper.selectOne(wrapper);
        if (existing == null) {
            // 新增
            CartItem item = new CartItem();
            item.setCartId(cartId);
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());
            cartItemMapper.insert(item);
        } else {
            // 更新数量
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemMapper.updateById(existing);
        }
    }

    @Override
    @Transactional
    public void updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null) {
            throw new BusinessException(404, "购物车项不存在");
        }
        // 验证该商品属于当前用户
        Cart cart = getById(item.getCartId());
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }
        item.setQuantity(request.getQuantity());
        cartItemMapper.updateById(item);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null) return;
        Cart cart = getById(item.getCartId());
        if (!cart.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作");
        }
        cartItemMapper.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Long cartId = getOrCreateCartId(userId);
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getCartId, cartId);
        cartItemMapper.delete(wrapper);
    }
}