package com.future.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.reggie.common.BaseContext;
import com.future.reggie.common.CustomException;
import com.future.reggie.entity.*;
import com.future.reggie.mapper.OrderMapper;
import com.future.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guorui
 * @create 2022-12-12-10:12
 */

@Service
@Slf4j

public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        if (list == null  || list.size() == 0){
            throw new CustomException("购物车数据为空，不能下单");
        }

        //通过IdWorker工具类自动生成orderid
        Long orderId = IdWorker.getId();
        List<OrderDetail> listD = new ArrayList<>();
        //设置变量计算购物车商品的总金额，使用AtomicInteger可以确保在多线程情况下，数据也是准确安全的
        AtomicInteger amount = new AtomicInteger(0);
        //遍历购物车的同时也把订单明细数据封装好
        for (ShoppingCart s : list){
            OrderDetail od = new OrderDetail();
            od.setOrderId(orderId);   //订单Id
            od.setNumber(s.getNumber());   //菜品数量，从购物车中获取
            od.setDishFlavor(s.getDishFlavor());  //菜品口味，从购物车中获取
            od.setDishId(s.getDishId());  //菜品id，从购物车中获取
            od.setSetmealId(s.getSetmealId());  //套餐id，从购物车中获取
            od.setName(s.getName());  //菜品或套餐名称，从购物车中获取
            od.setImage(s.getImage()); //菜品或套餐图片，从购物车中获取
            od.setAmount(s.getAmount());  //菜品或套餐价格，从购物车中获取
            amount.addAndGet(s.getAmount().multiply(new BigDecimal(s.getNumber())).intValue());  //总金额=sum(每个菜品单价*每个菜品数量)
            listD.add(od);   //将订单明细数据加到集合中
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null){
            throw new CustomException("用户地址为空，不能下单");
        }
        //向订单主表插入数据
        //将订单数据填充完整

        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);  //状态2表示等待派送
        orders.setAmount(new BigDecimal(amount.get()));   //获取前面从购物车数据计算好的总金额数据amount
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(user.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                        + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                        + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                        + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);
        //向订单明细表插入数据
        orderDetailService.saveBatch(listD);
        //清空购物车数据
        shoppingCartService.remove(lqw);
    }
}
