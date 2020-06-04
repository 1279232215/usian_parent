package com.usian;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-search-service")
public interface SearchFeign {


    //将数据库中item中数据导入到elasticsearch
    @RequestMapping("/service/searchItem/importAll")
    boolean importAll();
}
