package com.usian.controller;

import com.usian.pojo.TbItemDesc;
import com.usian.service.TbItemService;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/service/item")
public class ItemController {
    //端口业务层
    @Autowired
    private TbItemService tbItemService;

    /*
    * pojo     数据用@RequestBody
    * 基本数据  用@RequestParam
    *  restful 用@PathVariable
    * */

    //根据itemId查询TbItem数据
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInf(@RequestParam Long itemId){
        return tbItemService.selectItemInf(itemId);
    }

    //分页查询TbItem数据
    @RequestMapping("/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(@RequestParam Integer page,@RequestParam Long rows){
        return tbItemService.selectTbItemAllByPage(page,rows);
    }

    //添加数据TbItem,TbItemDesc,TbItemParam_item
    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem,@RequestParam String desc,@RequestParam String itemParams){
       return tbItemService.insertTbItem(tbItem,desc,itemParams);
    }

    //修改数据TbItem,TbItemDesc,TbItemParam_item
    @RequestMapping("/updateTbItem")
    public Integer updateTbItem(@RequestBody TbItem tbItem,String desc,String itemParams){
        return tbItemService.updateTbItem(tbItem,desc,itemParams);
    }
    //根据itemId删除TbItem
    @RequestMapping("/deleteItemById")
    public int deleteItemById(@RequestParam Long itemId){
       return tbItemService.deleteItemById(itemId);
    }

    @RequestMapping("/preUpdateItem")
    public Map<String,Object> preUpdateItem(@RequestParam Long itemId){
        return tbItemService.preUpdateItem(itemId);
    }

    //根据itemId查询TbItemDesc数据
    @RequestMapping("/selectItemDescByItemId")
    public TbItemDesc selectItemDescByItemId(@RequestParam Long itemId){
        return tbItemService.selectItemDescByItemId(itemId);
    }
}
