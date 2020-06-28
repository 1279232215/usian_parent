package com.usian.service;

import com.usian.utils.JsonUtils;
import com.usian.pojo.TbItem;
import com.usian.redis.RedisClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.usian.utils.CookieUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class CartServiceImpI implements CartService {


    @Value("${CART_REDIS_NAME}")
    private String CART_REDIS_NAME;


    @Autowired
    private RedisClient redisClient;

    //从redis中获取对应userId存的map
    @Override
    public Map<String, TbItem> getCartFromRedis(String userId) {
        Map<String, TbItem> cart = (Map<String, TbItem>) redisClient.hget(CART_REDIS_NAME, userId);
        if(cart.size()>0 && cart!=null){
            return cart;
        }
        return new HashMap<String, TbItem>();
    }

    //将map作为value，存到redis
    @Override
    public boolean addCartToRedis(String userId, Map<String, TbItem> map) {
        return redisClient.hset(CART_REDIS_NAME,userId,map);
    }

}
