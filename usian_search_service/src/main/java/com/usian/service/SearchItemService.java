package com.usian.service;

import com.usian.pojo.SearchItem;

import java.util.List;

public interface SearchItemService {
    boolean importAll();

    List<SearchItem> selectByQ(String q, Integer page, Integer pageSize);
}
