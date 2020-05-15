package com.usian.service;

import com.usian.pojo.TbItemCat;

import java.util.List;

public interface TbItemCatService {
    List<TbItemCat> selectItemCategoryByParentId(Long id);
}
