package com.future.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.reggie.dto.DishDto;
import com.future.reggie.entity.Dish;
import com.future.reggie.entity.DishFlavor;
import com.future.reggie.mapper.DishMapper;
import com.future.reggie.service.DishFlavorService;
import com.future.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guorui
 * @create 2022-12-06-14:41
 */

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional  //操作多张表加入事务处理标志
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //获得菜品Id
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> flavors2 = new ArrayList<>();
        for (DishFlavor f:flavors){
            f.setDishId(dishId);
            flavors2.add(f);
        }

        dishFlavorService.saveBatch(flavors2);
    }

    @Override
    public DishDto getWithFlavor(Long id) {
        //查询获得菜品基本信息
        Dish dish = this.getById(id);
        //查询获得菜品口味信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,id);
        //将菜品基本信息赋值给对象dto
        DishDto dto = new DishDto();
        BeanUtils.copyProperties(dish,dto);
        //将菜品口味信息赋值给dto
        List<DishFlavor> list = dishFlavorService.list(lqw);
        dto.setFlavors(list);
        return dto;
    }

    @Override
    @Transactional  //操作多张表加入事务处理标志
    public void updateWithFlavor(DishDto dishDto) {
        //修改dish表基本信息
        this.updateById(dishDto);
        //先清理原来的口味信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lqw);
        //再添加新的口味信息
        List<DishFlavor> list = dishDto.getFlavors();
        //为口味信息增加菜品id
        List<DishFlavor> flavors2 = new ArrayList<>();
        for (DishFlavor f:list){
            f.setDishId(dishDto.getId());
            flavors2.add(f);
        }

        dishFlavorService.saveBatch(flavors2);

    }

    @Override
    @Transactional  //操作多张表加入事务处理标志
    public void deleteWithFlavor(Long id) {
        //删除dish表基本信息
        this.removeById(id);
        //删除菜品口味信息
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(lqw);
    }
}
