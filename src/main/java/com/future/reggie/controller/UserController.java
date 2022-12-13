package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.future.reggie.common.R;
import com.future.reggie.common.ValidateCodeUtils;
import com.future.reggie.entity.User;
import com.future.reggie.service.UserService;
import com.future.reggie.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author guorui
 * @create 2022-12-09-14:10
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("手机验证码是:" + code);
            //调用阿里云提供的短信服务，发送验证码短信
            SMSUtils.sendMessage("郭睿的个人网站","SMS_263555312",phone,code);
            //生成的验证码保存到session中  保存手机号和user信息
            session.setAttribute(phone,"1234");
            //session.setAttribute(phone,code);
            return R.success("手机验证码发送成功");
        }
        return R.error("手机验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号和验证码
        String phone = map.get("phone").toString();
        //String code = map.get("code").toString();
        String code = "1234";
        //获取session中存储的验证码
        String sessioncode = (String) session.getAttribute(phone);
        //比对验证码
        if (sessioncode != null && sessioncode.equals(code)){
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            //通过手机号获取user对象
            User user = userService.getOne(lqw);
            //如果没有该用户则自动注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //在session中保存用户id
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
