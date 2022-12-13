package com.future.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.future.reggie.dto.DishDto;
import com.future.reggie.entity.Dish;

/**
 * @author guorui
 * @create 2022-12-06-14:40
 */
public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getWithFlavor(Long id);
    public void updateWithFlavor(DishDto dishDto);
    public void deleteWithFlavor(Long id);
}
