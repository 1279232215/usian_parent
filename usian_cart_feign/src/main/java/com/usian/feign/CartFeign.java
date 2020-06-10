package com.usian.feign;

import com.usian.pojo.TbItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-cart-service")
public interface CartFeign {


    //根据userId从redis获取map
    @RequestMapping("/service/cart/getCartFromRedis")
    Map<String, TbItem> getCartFromRedis(@RequestParam String userId);

    //根据userId从和map添加到redis中
    @RequestMapping("/service/cart/addCartToRedis")
    boolean addCartToRedis(Map<String, TbItem> map,@RequestParam String userId);
}
