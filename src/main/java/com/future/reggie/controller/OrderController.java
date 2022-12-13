package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.reggie.common.R;
import com.future.reggie.dto.DishDto;
import com.future.reggie.entity.Category;
import com.future.reggie.entity.Dish;
import com.future.reggie.entity.Orders;
import com.future.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author guorui
 * @create 2022-12-12-10:14
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：" + orders);
        orderService.submit(orders);
        return R.success("订单提交成功");
    }

    @GetMapping("/page")   //订单分页展示查询
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) throws ParseException {
        log.info("beginTime:" + beginTime);
        log.info("endTime:" + endTime);

        Page<Orders> pageInfo = new Page(page,pageSize);
        //DishDto类有CategoryName属性
        Page<Orders> dishPage = new Page<>();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper();
        lqw.like(number!=null,Orders::getNumber,number);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (beginTime!=null) {
            Date d1 = sdf1.parse(beginTime);
            lqw.ge(beginTime!=null,Orders::getOrderTime,d1);
        }
        if (endTime!=null) {
            Date d2 = sdf1.parse(endTime);
            lqw.le(endTime!=null,Orders::getOrderTime,d2);
        }


        lqw.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("修改订单信息");
        orderService.updateById(orders);
        return R.success("成功修改订单信息");
    }
}
