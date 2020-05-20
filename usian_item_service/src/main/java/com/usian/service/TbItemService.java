package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;

import java.util.Map;

public interface TbItemService {
    TbItem selectItemInf(Long itemId);

    PageResult selectTbItemAllByPage(Integer page, Long rows);

    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);

    int deleteItemById(Long itemId);

    Map<String, Object> preUpdateItem(Long itemId);
}
