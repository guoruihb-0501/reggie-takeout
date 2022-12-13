package com.future.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.reggie.common.CustomException;
import com.future.reggie.entity.Category;
import com.future.reggie.entity.Dish;
import com.future.reggie.entity.Employee;
import com.future.reggie.entity.Setmeal;
import com.future.reggie.mapper.CategoryMapper;
import com.future.reggie.mapper.EmployeeMapper;
import com.future.reggie.service.CategoryService;
import com.future.reggie.service.DishService;
import com.future.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guorui
 * @create 2022-12-06-10:10
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;


    @Override
    public void remove(Long id){
        //先查询要删除的分类是否关联了菜品或套餐，如果是，则报异常，不允许删除
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId,id);
        int c1 = dishService.count(lqw1);
        if (c1 > 0){
            throw new CustomException("要删除的分类已经有关联菜品");
        }
        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId,id);
        int c2 = setmealService.count(lqw2);
        if (c2 > 0){
            throw new CustomException("要删除的分类已经有关联套餐");
        }
        super.removeById(id);
    }
}
