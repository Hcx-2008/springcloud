package com.example.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hungcx
 * @create 2023-02-2023/2/2-23:34
 */
@Configuration
public class OpenFeignLogConfig {
    @Bean
    Logger.Level feignLoggerLevel(){
        //开启详细日志
        return Logger.Level.FULL;
    }
}
