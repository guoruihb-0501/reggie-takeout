package com.future.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.future.reggie.dto.DishDto;
import com.future.reggie.dto.SetmealDto;
import com.future.reggie.entity.Setmeal;

/**
 * @author guorui
 * @create 2022-12-06-14:40
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public SetmealDto getWithDish(Long id);
    public void updateWithDish(SetmealDto setmealDto);
    public void deleteWithDish(Long id);
}
