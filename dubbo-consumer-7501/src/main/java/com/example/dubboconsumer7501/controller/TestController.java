package com.example.dubboconsumer7501.controller;

import com.example.cloudalibabacommons.service.OrderService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: huangcx
 * @Date: 2023/3/5 - 03 - 05 - 4:11
 * @Description: com.example.dubboconsumer7501.controller
 * @Version: 1.0
 */
@RestController
public class TestController {

    @Reference
    private OrderService orderService;

    @GetMapping("/getOrder")
    public String getOrder() {
        return orderService.getOrder();
    }
}
