package com.usian.controller;

import com.netflix.discovery.converters.Auto;
import com.usian.pojo.SearchItem;
import com.usian.service.SearchItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/searchItem")
public class SearchItemController {
    @Autowired
    SearchItemService searchItemService;


    //导入item数据到elasticsearch
    @RequestMapping("/importAll")
    public boolean importAll(){
        return searchItemService.importAll();
    }

    //导入item数据到elasticsearch
    @RequestMapping("/selectByQ")
    public List<SearchItem> selectByQ(String q, Integer page, Integer pageSize){
        return searchItemService.selectByQ(q,page,pageSize);
    }
}
