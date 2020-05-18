package com.usian.controller;
import com.bjsxt.utils.Result;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItemParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/itemParam")
public class ItemParamController {
    @Autowired//接口对象
    private ItemServiceFeign itemServiceFeign;

    //查询商品规格模板，restful传参方式
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public Result selectItemParamByItemCatId(@PathVariable Long itemCatId){//接受result形式参数
       TbItemParam tbItemParam = itemServiceFeign.selectItemParamByItemCatId(itemCatId);//根据id查询
        if(tbItemParam!=null){//对象不为空时返回
            return Result.ok(tbItemParam);//返回查询对象
        }
        return Result.error("查询商品规格参数模板接口---查无结果");//返回错误数据
    }
}
