package com.future.reggie.dto;

import com.future.reggie.entity.Setmeal;
import com.future.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
