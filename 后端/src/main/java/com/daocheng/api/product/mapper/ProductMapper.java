package com.daocheng.api.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daocheng.api.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("""
            UPDATE product
            SET stock = stock - #{quantity}, updated_at = NOW()
            WHERE id = #{productId} AND stock >= #{quantity}
            """)
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("""
            UPDATE product
            SET stock = stock + #{quantity}, updated_at = NOW()
            WHERE id = #{productId}
            """)
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
