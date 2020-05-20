package com.usian.service;

import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;


public interface TbItemParamService {
    TbItemParam selectItemParamByItemCatId(Long itemCatId);

    PageResult selectItemParamAll(Integer page, Integer rows);

    int insertItemParam(Long itemCatId, String paramData);

    int deleteItemParamById(Long id);
}
