package com.future.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.reggie.dto.DishDto;
import com.future.reggie.dto.SetmealDto;
import com.future.reggie.entity.Dish;
import com.future.reggie.entity.DishFlavor;
import com.future.reggie.entity.Setmeal;
import com.future.reggie.entity.SetmealDish;
import com.future.reggie.mapper.SetmealMapper;
import com.future.reggie.service.SetmealDishService;
import com.future.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guorui
 * @create 2022-12-06-14:42
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        //保存套餐和菜品关联信息
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        List<SetmealDish> list = new ArrayList<>();
        for (SetmealDish d:dishes){
            //创建新的SetmealDish对象
            SetmealDish s = new SetmealDish();
            //注意setmealDto.getSetmealDishes()无法获得setmeal.id信息
            //逐个将setmealDto.getSetmealDishes()获取的setmealdish数据存到新的SetmealDish对象s中
            BeanUtils.copyProperties(d,s);
            //从setmealDto中获取setmeal.id信息给对象s
            s.setSetmealId(setmealDto.getId());
            //将完整的对象s加入到集合
            list.add(s);
        }
        setmealDishService.saveBatch(list);

    }

    @Override
    public SetmealDto getWithDish(Long id) {
        //查询获得套餐基本信息
        Setmeal setmal = this.getById(id);
        //查询获得套餐菜品关联信息
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        //将套餐基本信息赋值给对象dto
        SetmealDto dto = new SetmealDto();
        BeanUtils.copyProperties(setmal,dto);
        //将套餐菜品关联信息赋值给dto
        List<SetmealDish> list = setmealDishService.list(lqw);
        dto.setSetmealDishes(list);
        return dto;
    }

    @Override
    @Transactional  //操作多张表加入事务处理标志
    public void updateWithDish(SetmealDto setmealDto) {
        //修改setmeal表基本信息
        this.updateById(setmealDto);
        //先清理原来的菜品关联信息
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lqw);
        //再添加新的菜品关联信息
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        //为菜品信息增加套餐id
        List<SetmealDish> dishes2 = new ArrayList<>();
        for (SetmealDish f:list){
            f.setSetmealId(setmealDto.getId());
            dishes2.add(f);
        }
        setmealDishService.saveBatch(dishes2);
    }

    @Override
    @Transactional  //操作多张表加入事务处理标志
    public void deleteWithDish(Long id) {
        //删除setmeal表基本信息
        this.removeById(id);
        //删除套餐菜品关联信息
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(lqw);
    }
}
