package com.example.service.impl;

import com.example.cloudalibabacommons.utils.JsonResult;
import com.example.service.OpenFeignService;
import org.springframework.stereotype.Component;

/**
 * @author hungcx
 * @create 2023-02-2023/2/4-16:09
 */
@Component
public class OpenFeignServiceImpl implements OpenFeignService {

    @Override
    public JsonResult<String> msbSql(Long id) {
        return new JsonResult<>(444, null, "服务降级返回");
    }

    @Override
    public String timeOut() {
        return "服务降级返回";
    }
}
