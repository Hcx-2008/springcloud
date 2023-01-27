package com.example.nacosconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableDiscoveryClient
public class NacosconfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosconfigApplication.class, args);
    }

}
