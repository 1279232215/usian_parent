package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.usian.utils.Result;
@RestController
@RequestMapping("/frontend/itemCategory")
public class ItemCategoryController {

    @Autowired
    private ItemServiceFeign itemServiceFeign;


    //查询左侧商品分类目录
    @RequestMapping("/selectItemCategoryAll")
    public Result selectItemCategoryAll(){
        CatResult catResult = itemServiceFeign.selectItemCategoryAll();//返回自定义包装类
        if(catResult!=null && catResult.getData().size()>0){
            return Result.ok(catResult);
        }
        return Result.error("左侧商品分类目录===查询失败!!!");
    }

}
