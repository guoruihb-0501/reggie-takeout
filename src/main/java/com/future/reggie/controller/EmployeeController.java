package com.future.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.future.reggie.common.R;
import com.future.reggie.entity.Employee;
import com.future.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.dsig.DigestMethod;
import java.time.LocalDateTime;

/**
 * @author guorui
 * @create 2022-11-29-8:47
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;


    // @RequestBody 接收前台传来的json形式的data数据
    // 包含username和password
    // 和实体类Employee的username和password相对应
    // HttpServletRequest 用于将返回的员工id保存到request的session中
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 1.将传入的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 2.根据用户名来查询数据库
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper();
        lqw.eq(Employee::getUsername ,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);
        // 3.如果没有查询到则返回错误
        if (emp == null){
            return R.error("没有查到用户" + employee.getUsername());
        }

        // 4.密码比对
        if (!emp.getPassword().equals(password)){
            return R.error("密码输入错误");
        }
        Integer status = emp.getStatus();
        System.out.println(status);
        // 5.查看员工状态是否可用
        if (!emp.getStatus().equals(1)){
            return R.error("员工被禁止登录系统");
        }

        // 登录成功，将员工的Id存入session
        request.getSession().setAttribute("employee",emp.getId());

        // 7.登录成功返回
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 1.清理session中的id信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));  //设置初始密码123456，并用md5加密
        //从session中获取用户id，作为创建人id或修改人id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setCreateUser(empId);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //上面的数据为每个表都要填写的内容，改由MyMetaObjectHandler类的方法自动填充实现
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> getPage(int page,int pageSize, String name){  //name 为查询条件，可以为空
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //添加条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name); //条件不为空则添加查询条件
        lqw.orderByDesc(Employee::getUpdateTime);  //按更新时间倒序排序
        //执行查询
        employeeService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee) {
        log.info(employee.toString());
        //employee.setUpdateTime(LocalDateTime.now());
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息已经修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id进行查询");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
