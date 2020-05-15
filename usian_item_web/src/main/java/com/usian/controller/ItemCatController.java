package com.usian.controller;

import com.bjsxt.utils.Result;
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
    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @RequestMapping("/selectItemCategoryByParentId")
    public Result selectItemCategoryByParentId(@RequestParam(value = "id",defaultValue = "0")Long id){
        List<TbItemCat> itemCateList= itemServiceFeign.selectItemCategoryByParentId(id);
        if(itemCateList!=null && itemCateList.size()>0){
            return Result.ok(itemCateList);
        }
        return Result.error("查询商品类目接口，查无数据");
    }
}
