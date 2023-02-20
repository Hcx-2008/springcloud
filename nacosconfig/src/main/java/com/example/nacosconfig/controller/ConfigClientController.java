package com.example.nacosconfig.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.nacosconfig.handler.CustomerBlockHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RefreshScope //支持Nacos的动态刷新功能
public class ConfigClientController {

    @Value("${config.info}")
    private String configInfo;

    @Value("${config.common}")
    private String configCommon;

    //共享
    @Value("${redisip}")
    private String redisIp;

    @GetMapping("/config/info")
    public String getConfigInfo(){
        System.out.println(configInfo);
        return configInfo;
    }

    @GetMapping("/config/common")
    public String getConfigCommon(){
        System.out.println(configCommon);
        return configCommon;
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

    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "handler_HotKey")
    public String testHotKey(@RequestParam(value = "hot1",required = false) String hot1,
                             @RequestParam(value = "hot2",required = false)String hot2,
                             @RequestParam(value = "hot13",required = false) String hot3){
        return "----testHotKey,热点规则测试";
    }

    //处理异常方法，方法签名要和对应的接口方法保持一致
    public String handler_HotKey(String hot1, String hot2, String hot3, BlockException exception){
        return "系统繁忙稍后重试。。";
    }

    @GetMapping("/byRest")
    @SentinelResource(value = "byRest")
    public String byRest(){
        // 自定义限流测试
        return "-----byRest";
    }

    /**
     * 此方法用到了自定义限流处理类型CustomerBlockHandler
     * 中的handlerException1方法来处理限流逻辑。
     */
    @GetMapping("/bycustomer")
    @SentinelResource(value = "bycustomer",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handlerException1")
    public String bycustomer(){
        return "-----bycustomer";
    }

}