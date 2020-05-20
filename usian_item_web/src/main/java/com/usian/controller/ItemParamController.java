package com.usian.controller;
import com.bjsxt.utils.Result;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //分页查询商品规格
    @RequestMapping("/selectItemParamAll")
    public Result selectItemParamAll(@RequestParam(defaultValue = "1")Integer page,@RequestParam(defaultValue = "3")Integer rows){
        PageResult pageResult = itemServiceFeign.selectItemParamAll(page,rows); //查询出商品规格返回自定义pageResult类
        if(pageResult.getResult().size()>0){  //判断返回集合是否为空
            return Result.ok(pageResult);
        }
        return Result.error("分页查询商品规格出错！！！");
    }

    //添加商品规格
    @RequestMapping("/insertItemParam")
    public Result insertItemParam(Long itemCatId,String paramData){
        int i = itemServiceFeign.insertItemParam(itemCatId,paramData);//去添加商品规格模板，执行成功返回1、
        if(i==1){     //判断添加是否成功
            return Result.ok();
        }else if(i==0){
            return Result.error("添加失败!!!==已存在对应模板==");
        }
        return Result.error("商品规格模板添加失败!!!");
    }

    //根据商品id删除商品规格
    @RequestMapping("/deleteItemParamById")
    public Result deleteItemParamById(Long id){
        int i = itemServiceFeign.deleteItemParamById(id);//根据商品id删除商品规格，执行成功返回1、
        if(i==1){//判断删除是否成功
            return Result.ok();
        }
        return Result.error("商品规格模板删除失败!!!");
    }
}
