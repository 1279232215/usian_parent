package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.bjsxt.utils.Result;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/item")
public class ItemController {

    //feign接口
    @Autowired
    private ItemServiceFeign itemServiceFeign;

    /**
     * 根据id查询商品基本信息
     * @param itemId
     * @return
     */
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        System.out.println(itemId+"======================");
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if(tbItem!=null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }
    /**
     * 分页查询TbItem商品数据
     *  @param page 当前页
     *  @param rows 当前页展示几条
     *  @return
     */
    @RequestMapping("selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(value = "page",defaultValue = "1",required = true) Integer page,
                                        @RequestParam(value = "rows",defaultValue = "2",required = true) Long rows){
        PageResult pageResult = itemServiceFeign.selectTbItemAllByPage(page,rows);//去调用feign接口
        if(pageResult.getResult()!=null && pageResult.getResult().size()>0){//判断PageResult类中返回的结果集不为空并且list集合不小于0
           return Result.ok(pageResult);//返回前台
        }
        return Result.error("查无数据!!!");//当判断失败时返回前台
    }
}
