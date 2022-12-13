package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.future.reggie.common.BaseContext;
import com.future.reggie.common.R;
import com.future.reggie.entity.ShoppingCart;
import com.future.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author guorui
 * @create 2022-12-11-8:30
 */

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据："+ shoppingCart);
        //设置用户id，指定是哪个用户的购物车信息
        Long userid = BaseContext.getCurrentId();
        shoppingCart.setUserId(userid);


        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userid);

        if (dishId != null){
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else{
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品或套餐是否已经在购物车中
        ShoppingCart cart = shoppingCartService.getOne(lqw);
        if (cart != null){
            //如果在，则原来数量基础+1
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            shoppingCartService.updateById(cart);
        }else {
            //如果不在，则新增购物车信息
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }

        return R.success(cart);
    }


    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据："+ shoppingCart);
        //设置用户id，指定是哪个用户的购物车信息
        Long userid = BaseContext.getCurrentId();
        shoppingCart.setUserId(userid);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userid);

        if (dishId != null){
            lqw.eq(ShoppingCart::getDishId,dishId);
        }else{
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品或套餐是否已经在购物车中
        ShoppingCart cart = shoppingCartService.getOne(lqw);

        Integer number = cart.getNumber();
        if (number > 1){
            //如果在，则原来数量基础-1
            cart.setNumber(number - 1);
            shoppingCartService.updateById(cart);
        }else {
            //如果只有1个，则删掉本购物车
            shoppingCartService.remove(lqw);
            //cart = shoppingCart;
        }

        return R.success("购物车删除成功");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        Long userid = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userid);
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);

    }

    @DeleteMapping("clean")
    public R<String> clean(){

        log.info("清空购物车");
        Long userid = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userid);

        shoppingCartService.remove(lqw);
        return R.success("购物车清空成功");
    }
}
