package com.example.cloudalibabagateway8888;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableDiscoveryClient
public class CloudalibabaConsumer8888Application {

    public static void main(String[] args) {
        SpringApplication.run(CloudalibabaConsumer8888Application.class, args);
    }
}
