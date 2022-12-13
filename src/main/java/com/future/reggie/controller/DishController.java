package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.reggie.common.R;
import com.future.reggie.dto.DishDto;
import com.future.reggie.entity.Category;
import com.future.reggie.entity.Dish;
import com.future.reggie.entity.DishFlavor;
import com.future.reggie.service.CategoryService;
import com.future.reggie.service.DishFlavorService;
import com.future.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author guorui
 * @create 2022-12-07-14:51
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody  DishDto dishDto){  //@requestBody接收json类型数据
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")   //菜品分页展示查询
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page(page,pageSize);
        //DishDto类有CategoryName属性
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        lqw.like(name!=null,Dish::getName,name);
        lqw.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,lqw);   //现在Page<Dish> pageInfo已经有了数据，但是Dish类中不包含CategoryName属性，所以需要再对dishDtoPage赋值
        //将pageInfo中的所有属性复制给dishDtoPage，复制时忽略records属性，其他属性进行复制
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        //records为List<Dish>集合，list为List<DishDto>集合，现在list为空
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();

        //给dishDtoPage的categoryname属性赋值
        for (Dish d:records){

            DishDto dto = new DishDto();
            //将Dish类对象d的内容赋值给DishDto类对象dto
            BeanUtils.copyProperties(d,dto);
            //获取Dish类对象d的categoryId属性
            Long categoryId = d.getCategoryId();
            //通过categoryId属性获取categoryName属性，并赋值给DishDto类对象dto
            Category category = categoryService.getById(categoryId);
            dto.setCategoryName(category.getName());
            //将DishDto类对象dto添加到集合list中
            list.add(dto);
        }
        //将集合List<DishDto> list(此时list对象中的categoryName属性已经有值)，赋值给dishDtoPage并返回
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dto = dishService.getWithFlavor(id);
        return R.success(dto);
    }

    @DeleteMapping
    public R<String> delete(Long[] ids){
        log.info(ids.toString());
        /*if (ids.length == 1) {
            dishService.deleteWithFlavor(ids[0]);
        }*/
        for (Long id:ids){
            dishService.deleteWithFlavor(id);
        }

        return R.success("删除菜品成功");
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
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
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Dish::getId, id);
            dishService.update(dish, lqw);
        }
        return R.success("修改菜品状态成功");
    }

    /*@GetMapping("/list")
    public R<List<Dish>> getDish(Dish dish){
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lqw.eq(Dish::getStatus,1);  //只查状态=1的菜品
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        return R.success(list);
    }*/

    @GetMapping("/list")
    public R<List<DishDto>> getDish(Dish dish){
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        lqw.eq(Dish::getStatus,1);  //只查状态=1的菜品
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish d:list){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(d,dishDto);
            Long dishId = d.getId();
            LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw2);
            dishDto.setFlavors(dishFlavorList);
            dishDtoList.add(dishDto);
        }

        return R.success(dishDtoList);
    }

}
