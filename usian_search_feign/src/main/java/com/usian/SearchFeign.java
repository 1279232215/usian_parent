package com.usian;

import com.usian.pojo.SearchItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("usian-search-service")
public interface SearchFeign {


    //将数据库中item中数据导入到elasticsearch
    @RequestMapping("/service/searchItem/importAll")
    boolean importAll();

    //前台搜索接口
    @RequestMapping("/service/searchItem/selectByQ")
    List<SearchItem> selectByQ(@RequestParam String q,@RequestParam Integer page,@RequestParam Integer pageSize);
}
