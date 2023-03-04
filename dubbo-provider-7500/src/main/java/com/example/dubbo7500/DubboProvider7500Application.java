package com.example.dubbo7500;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.example.dubbo7500")
@EnableDiscoveryClient
public class DubboProvider7500Application {

    public static void main(String[] args) {
        SpringApplication.run(DubboProvider7500Application.class, args);
    }

}
