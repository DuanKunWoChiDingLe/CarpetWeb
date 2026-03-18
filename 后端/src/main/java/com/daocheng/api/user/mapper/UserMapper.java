package com.daocheng.api.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daocheng.api.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}