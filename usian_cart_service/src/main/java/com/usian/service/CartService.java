package com.usian.service;

import com.usian.pojo.TbItem;

import java.util.Map;

public interface CartService {
    Map<String, TbItem> getCartFromRedis(String userId);

    boolean addCartToRedis(String userId, Map<String, TbItem> map);
}
