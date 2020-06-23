package com.usian.controller;

import com.usian.utils.Result;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItemCat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/itemCategory")
public class ItemCatController {

    @Autowired//接口对象
    private ItemServiceFeign itemServiceFeign;

    // 根据parentId查询出TbItemCat,做类目功能
    @RequestMapping("/selectItemCategoryByParentId")//parentId应为刚开始是从父级查的所以默认参数设置为0
    public Result selectItemCategoryByParentId(@RequestParam(value = "id",defaultValue = "0")Long id){
        List<TbItemCat> itemCateList= itemServiceFeign.selectItemCategoryByParentId(id);//根据parentId查询出TbItemCat为list集合
        if(itemCateList!=null && itemCateList.size()>0){//判断集合是否为空,并且大小大于0
            return Result.ok(itemCateList);//返回前台查询出的类目数据
        }
        return Result.error("查询商品类目接口，查无数据");//返回错误数据
    }
}
