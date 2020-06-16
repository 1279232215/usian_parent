package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.CartService;
import com.usian.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/service/cart")
public class CartServiceController {

    @Autowired
    private CartService cartService;

    @RequestMapping("/getCartFromRedis")
    public Map<String, TbItem> getCartFromRedis(String userId){
        return  cartService.getCartFromRedis(userId);
    }

    @RequestMapping("/addCartToRedis")
    public boolean addCartToRedis(String userId,@RequestBody Map<String, TbItem> map){
        return cartService.addCartToRedis(userId,map);
    }

}
