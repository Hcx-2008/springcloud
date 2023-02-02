package com.example.controller;

import com.example.cloudalibabacommons.utils.JsonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
public class DataController {
    @Value("${server.port}")
    private String serverPort;

    //模仿数据库存储数据
    public static HashMap<Long,String> hashMap = new HashMap<>();
    static {
        hashMap.put(1L,"鼠标");
        hashMap.put(2L,"键盘");
        hashMap.put(3L,"耳机");
    }

    @GetMapping("info/{id}")
    public JsonResult<String> msbSql(@PathVariable("id") Long id){
        JsonResult<String> result = new JsonResult(200, hashMap.get(id), serverPort);
        return result;
    }

    @GetMapping("/timeOut")
    public String timeOut() {
        try {
            System.out.println("延迟响应");
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }
}