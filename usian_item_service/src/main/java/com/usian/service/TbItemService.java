package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;

public interface TbItemService {
    /**
     * 根据id查询商品基本信息
     * @param itemId
     * @return
     */
    TbItem selectItemInf(Long itemId);
    /**
     * 分页查询TbItem商品数据
     *  @param page 当前页
     *  @param rows 当前页展示几条
     *  @return
     */
    PageResult selectTbItemAllByPage(Integer page, Long rows);

    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);

    int deleteItemById(Long itemId);
}
