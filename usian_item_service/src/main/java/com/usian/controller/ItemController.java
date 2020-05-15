package com.usian.controller;

import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import com.usian.service.TbItemService;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/item")
public class ItemController {
    //端口业务层
    @Autowired
    private TbItemService tbItemService;
    /**
     * 根据id查询商品基本信息
     * @param itemId
     * @return
     */
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInf(@RequestParam Long itemId){
        return tbItemService.selectItemInf(itemId);
    }
    /**
     * 分页查询TbItem商品数据
     *  @param page 当前页
     *  @param rows 当前页展示几条
     *  @return
     */
    @RequestMapping("/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(@RequestParam Integer page,@RequestParam Long rows){
        return tbItemService.selectTbItemAllByPage(page,rows);//去service业务逻辑层处理业务
    }
}
