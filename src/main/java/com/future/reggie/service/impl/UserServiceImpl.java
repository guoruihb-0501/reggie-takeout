package com.future.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.reggie.entity.User;
import com.future.reggie.mapper.UserMapper;
import com.future.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author guorui
 * @create 2022-12-09-14:09
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
