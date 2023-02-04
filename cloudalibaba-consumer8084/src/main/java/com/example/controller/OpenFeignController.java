package com.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.cloudalibabacommons.utils.JsonResult;
import com.example.service.OpenFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenFeignController {

    @Autowired
    private OpenFeignService openFeignService;

    @GetMapping("getInfo/{id}")
    @SentinelResource(value = "getInfo")
    public JsonResult<String> getInfo(@PathVariable("id") Long id){
        return openFeignService.msbSql(id);
    }

    public JsonResult<String> handlerResource(Long id, BlockException exception){
        return new JsonResult<>(444, id + "", "系统繁忙");
    }

    @GetMapping("/timeOut")
    public String timeOut() {return openFeignService.timeOut();}

}