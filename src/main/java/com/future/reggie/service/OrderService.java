package com.future.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.future.reggie.entity.Orders;

/**
 * @author guorui
 * @create 2022-12-12-10:11
 */

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}
