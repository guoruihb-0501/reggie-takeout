package com.future.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.future.reggie.entity.Category;

/**
 * @author guorui
 * @create 2022-12-06-10:09
 */
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
