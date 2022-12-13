package com.future.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author guorui
 * @create 2022-12-06-14:38
 */

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
