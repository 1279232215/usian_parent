package com.usian.controller;

import com.usian.pojo.TbItemCat;
import com.usian.service.TbItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/itemCat")
public class ItemCatController {
    //端口业务层
    @Autowired
    private TbItemCatService tbItemCatService;

    //查询商品类目
    @RequestMapping("/selectItemCategoryByParentId")
    public List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id){
        return tbItemCatService.selectItemCategoryByParentId(id);
    }
 }
