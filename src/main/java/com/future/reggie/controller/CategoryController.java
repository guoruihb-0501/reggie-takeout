package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.reggie.common.R;
import com.future.reggie.entity.Category;
import com.future.reggie.entity.Employee;
import com.future.reggie.service.CategoryService;
import com.future.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author guorui
 * @create 2022-12-06-10:19
 */

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("增加菜品或套餐分类");
        categoryService.save(category);
        return R.success("菜品或套餐分类增加成功");
    }

    @GetMapping("/page")
    public R<Page> getPage(int page, int pageSize){  //name 为查询条件，可以为空
        log.info("page = {},pageSize = {},name = {}",page,pageSize);
        //构造分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);
        //添加条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        lqw.orderByDesc(Category::getSort);  //按sort字段排序
        //执行查询
        categoryService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> deleteById(Long id){
        log.info("删除菜品分类或套餐分类 {}",id);
        categoryService.remove(id);
        return R.success("删除菜品分类或套餐分类成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息");
        categoryService.updateById(category);
        return R.success("成功修改分类信息");
    }

    @GetMapping("/list")  //用于获取分类下拉列表信息，菜品和套餐编辑页面中需要使用分类下拉框
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        if (category.getType() == null) {
            lqw.ne(Category::getType,"3");
        }else {
            lqw.eq(Category::getType, category.getType());
        }
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }
}
