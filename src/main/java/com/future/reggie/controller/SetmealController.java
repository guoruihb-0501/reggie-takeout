package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.reggie.common.R;
import com.future.reggie.dto.DishDto;
import com.future.reggie.dto.SetmealDto;
import com.future.reggie.entity.Category;
import com.future.reggie.entity.Dish;
import com.future.reggie.entity.Setmeal;
import com.future.reggie.service.CategoryService;
import com.future.reggie.service.SetmealDishService;
import com.future.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guorui
 * @create 2022-12-08-15:19
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐保存成功");
    }

    @GetMapping("/page")   //套餐分页展示查询
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page(page,pageSize);
        //DishDto类有CategoryName属性
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper();
        lqw.like(name!=null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,lqw);   //现在Page<Setmeal> pageInfo已经有了数据，但是Setmeal类中不包含CategoryName属性，所以需要再对setmealDtoPage赋值
        //将pageInfo中的所有属性复制给setmealDtoPage，复制时忽略records属性，其他属性进行复制
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        //records为List<Setmeal>集合，list为List<SetmealDto>集合，现在list为空
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = new ArrayList<>();

        //给setmealDtoPage的categoryname属性赋值
        for (Setmeal d:records){

            SetmealDto dto = new SetmealDto();
            //将Setmeal类对象d的内容赋值给SetmealDto类对象dto
            BeanUtils.copyProperties(d,dto);
            //获取Setmeal类对象d的categoryId属性
            Long categoryId = d.getCategoryId();
            //通过categoryId属性获取categoryName属性，并赋值给SetmealDto类对象dto
            Category category = categoryService.getById(categoryId);
            dto.setCategoryName(category.getName());
            //将DishDto类对象dto添加到集合list中
            list.add(dto);
        }
        //将集合List<SetmealDto> list(此时list对象中的categoryName属性已经有值)，赋值给setmealDtoPage并返回
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto dto = setmealService.getWithDish(id);
        return R.success(dto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids){
        log.info(ids.toString());
        /*if (ids.length == 1) {
            dishService.deleteWithFlavor(ids[0]);
        }*/
        for (Long id:ids){
            setmealService.deleteWithDish(id);
        }
        return R.success("删除套餐成功");
    }

    @PostMapping("/status/{status}")
    public R<String> change(@PathVariable int status,Long[] ids){
        log.info(ids.toString());
        /*if (ids.length == 1) {
            Dish dish = new Dish();
            dish.setId(ids[0]);
            dish.setStatus(status);
            LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Dish::getId, ids[0]);
            dishService.update(dish, lqw);
        }*/

        for (Long id:ids){
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(status);
            LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Setmeal::getId, id);
            setmealService.update(setmeal, lqw);
        }
        return R.success("修改套餐状态成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> getSetmeal(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(Setmeal::getStatus,1);  //只查状态=1的菜品
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lqw);
        return R.success(list);
    }
}
