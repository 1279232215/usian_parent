package com.usian.controller;

import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemParam;
import com.usian.service.TbItemCatService;
import com.usian.service.TbItemParamService;
import com.usian.service.TbItemService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/itemParam")
public class ItemParamController {
    //端口业务层
    @Autowired
    private TbItemParamService tbItemParamService;

    //查询商品规格参数
    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemCategoryByParentId(@PathVariable Long itemCatId){
        return tbItemParamService.selectItemParamByItemCatId(itemCatId);
    }

    //分页查询商品规格参数
    @RequestMapping("/selectItemParamAll")
    public PageResult selectItemParamAll(Integer page,Integer rows){
        return tbItemParamService.selectItemParamAll(page,rows);
    }

    //添加商品规格
    @RequestMapping("/insertItemParam")
    public int insertItemParam(Long itemCatId,String paramData){
        return tbItemParamService.insertItemParam(itemCatId,paramData);
    }

    //根据id删除商品规格
    @RequestMapping("/deleteItemParamById")
    public int insertItemParam(Long id){
        return tbItemParamService.deleteItemParamById(id);
    }
 }
