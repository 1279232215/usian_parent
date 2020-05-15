package com.usian.controller;

import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemParam;
import com.usian.service.TbItemCatService;
import com.usian.service.TbItemParamService;
import com.usian.service.TbItemService;
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

    @RequestMapping("/selectItemParamByItemCatId/{itemCatId}")
    public TbItemParam selectItemCategoryByParentId(@PathVariable Long itemCatId){
        return tbItemParamService.selectItemParamByItemCatId(itemCatId);
    }
 }
