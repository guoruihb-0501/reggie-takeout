package com.future.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author guorui
 * @create 2022-12-03-8:56
 */
//指定拦截哪些controller类来抛异常，因为每个controller都加了@RestController注解，所以这样就可以拦截
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
//为了返回json数据，需要加下面的@responsebody注解
@ResponseBody
public class GlobalExceptionHandler {

    //指定处理哪些异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            return R.error("录入的信息违反了唯一约束 ");
        }
        return R.error("未知错误 ");
    }

    //指定处理哪些异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
