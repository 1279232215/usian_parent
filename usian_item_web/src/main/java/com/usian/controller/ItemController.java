package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.bjsxt.utils.Result;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    //feign接口
    @Autowired
    private ItemServiceFeign itemServiceFeign;

    //根据id查询商品基本信息
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);//传去itemId查出Item
        if(tbItem!=null){             //判断返回值是否为null
            return Result.ok(tbItem); //返回查出对象
        }
        return Result.error("查无结果");//返回错误提示
    }

    // 分页查询TbItem商品数据
    @RequestMapping("selectTbItemAllByPage")//page是当前页，默认为第1页，rows是当前展示几条，默认为2条
    public Result selectTbItemAllByPage(@RequestParam(value = "page",defaultValue = "1",required = true) Integer page,
                                        @RequestParam(value = "rows",defaultValue = "2",required = true) Long rows){
        PageResult pageResult = itemServiceFeign.selectTbItemAllByPage(page,rows);//去调用feign接口,实现查询
        if(pageResult!=null && pageResult.getResult().size()>0){//判断PageResult不为空并且类中返回的结果集list集合不小于0
           return Result.ok(pageResult);                //讲定义的分页类返回到前太
        }
        return Result.error("查无数据!!!");//返回错误提示
    }

    //添加TbItem和TbDesc和TbParam_Item表数据,表现层接受参数，前台不是json传就不能用@requestBody
    @RequestMapping("/insertTbItem")
    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer s = itemServiceFeign.insertTbItem(tbItem,desc,itemParams);//因为插入3个表中的数据，是3条insert所以返回为3
        if(s==3){//判断返回值是否是3
            return Result.ok();//返回正确为前台
        }
        return Result.error("添加失败");//返回错误提示
    }

    @RequestMapping("/deleteItemById")
    public Result deleteItemById(Long itemId){
        int i = itemServiceFeign.deleteItemById(itemId);//去调用删除，一个delete语句所以返回1
        if(i==1){//判断返回是否为1
            return Result.ok();//返回前台删除正确
        }
        return Result.error("删除失败");//返回错误提示
    }
}
