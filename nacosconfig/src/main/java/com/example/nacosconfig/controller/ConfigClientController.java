package com.example.nacosconfig.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RefreshScope //支持Nacos的动态刷新功能
public class ConfigClientController {

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/config/info")
    public String getConfigInfo(){
        System.out.println(configInfo);
        return configInfo;
    }

    @GetMapping("/testc")
    public String testC(){
        try {
            // 慢比例测试Demo
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "----testC";
    }


    @GetMapping("/testD")
    public String testD(Integer id){
        if(id != null && id > 1){
            throw new RuntimeException("异常比例测试");
        }
        return "------------testD";
    }

}