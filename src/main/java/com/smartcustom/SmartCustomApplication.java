package com.smartcustom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智能客服系统 - 主应用程序类
 * 
 * @author SmartCustom Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class SmartCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCustomApplication.class, args);
    }
}