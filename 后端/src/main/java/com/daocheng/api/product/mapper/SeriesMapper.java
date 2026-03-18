package com.daocheng.api.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daocheng.api.product.entity.Series;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeriesMapper extends BaseMapper<Series> {

    // 可选：关联查询品牌
    @Select("SELECT s.*, b.id as \"brand.id\", b.name as \"brand.name\", b.logo as \"brand.logo\" " +
            "FROM series s LEFT JOIN brand b ON s.brand_id = b.id WHERE s.id = #{id}")
    Series selectSeriesWithBrand(Long id);

    // 分页查询系列列表（可结合品牌过滤），建议使用 MyBatis-Plus 的 Page 和条件构造器，无需手写 SQL
}