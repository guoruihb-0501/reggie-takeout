package com.future.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author guorui
 * @create 2022-12-09-14:08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
