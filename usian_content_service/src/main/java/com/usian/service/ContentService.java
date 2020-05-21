package com.usian.service;

import com.usian.pojo.TbContent;
import com.usian.utils.PageResult;

public interface ContentService {
    PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId);

    int insertTbContent(TbContent tbContent);

    int deleteContentByIds(Long id);
}
