package com.example.dubbo7500.service;

import com.example.cloudalibabacommons.service.OrderService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @Auther: huangcx
 * @Date: 2023/3/3 - 03 - 03 - 11:59
 * @Description: com.example.dubbo7500.service
 * @Version: 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrder() {
        return "dubbo调用成功！";
    }
}
