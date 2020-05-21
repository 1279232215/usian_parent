package com.usian.service;

import com.usian.pojo.TbContentCategory;

import java.util.List;

public interface ContentCategoryService {
    List<TbContentCategory> selectContentCategoryByParentId(Long id);

    int insertContentCategory(TbContentCategory tbContentCategory);

    int deleteContentCategoryById(Long categoryId);

    int updateContentCategory(Long id, String name);
}
