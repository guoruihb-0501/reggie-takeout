package com.future.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author guorui
 * @create 2022-12-12-10:09
 */

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
