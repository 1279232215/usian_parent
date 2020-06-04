package com.usian.controller;

import  com.bjsxt.utils.Result;
import com.usian.SearchFeign;
import com.usian.pojo.SearchItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchItemController {
    @Autowired
    private SearchFeign searchFeign;


    //导入item数据到elasticsearch中
    @RequestMapping("/importAll")
    public Result importAll(){
        boolean b = searchFeign.importAll();
        if(b){
            return Result.ok();
        }
        return Result.error("导入商品到elasticSearch索引库失败！！！");
    }

}
