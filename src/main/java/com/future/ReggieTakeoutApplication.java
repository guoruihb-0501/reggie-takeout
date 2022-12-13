package com.future;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.annotation.ServletSecurity;

@Slf4j
@SpringBootApplication
@EnableScheduling
@MapperScan("com.future.reggie.mapper")
@ServletComponentScan
@EnableTransactionManagement
public class ReggieTakeoutApplication {

    public static void main(String[] args) {

        SpringApplication.run(ReggieTakeoutApplication.class, args);
        log.info("项目启动成功");
    }

}
