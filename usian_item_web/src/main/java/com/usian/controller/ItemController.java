package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.bjsxt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    //feign接口
    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /**
     * 查询商品基本信息
     */
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        System.out.println(itemId+"======================");
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if(tbItem!=null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }
}
