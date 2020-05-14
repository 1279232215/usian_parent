package com.usian.controller;

import com.netflix.discovery.converters.Auto;
import com.usian.service.TbItemService;
import com.usian.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/item")
public class ItemController {
    @Autowired
    private TbItemService tbItemService;
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInf(@RequestParam Long itemId){
        return tbItemService.selectItemInf(itemId);
    }
}
