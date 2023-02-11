package com.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.cloudalibabacommons.utils.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class DemoController {
    //服务提供者URL
    @Value("${service-url.nacos-user-service}")
    private String serviceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/consumer/fallback/{id}")
    //同时添加SentinelResource注解的fallback和blockHandler属性
    @SentinelResource(value = "fallback", fallback = "fallbackHandler", blockHandler = "blockHandler")
    public JsonResult<String> fallback(@PathVariable Long id) {
        if (id <= 3) {
            //通过Ribbon发起远程访问，访问9003/9004
            JsonResult<String> result = restTemplate.getForObject(serviceUrl + "/info/" + id, JsonResult.class);
            System.err.println(result.getData());
            return result;
        } else {
            throw new NullPointerException("没有对应的数据记录");
        }
    }

    //处理Java异常
    public JsonResult<String> fallbackHandler(Long id, Throwable e) {
        JsonResult<String> result = new JsonResult<>(444, "NullPointerException异常", e.getMessage());
        return result;
    }

    //处理Sentinel限流
    public JsonResult<String> blockHandler(Long id, BlockException e) {
        JsonResult<String> result = new JsonResult<>(445, "BlockException", e.getMessage());
        return result;
    }
}