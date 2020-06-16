package com.usian.controller;

import com.bjsxt.utils.Result;
import com.usian.feign.CartFeign;
//import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frontend/order")
public class OrderWebController {

    @Autowired
    private CartFeign cartFeign;

//    @Autowired
//    private OrderServiceFeign orderServiceFeign;


    //点击去结算,根据前台勾选商品id查找返回前台结算数据
    @RequestMapping("/goSettlement")
    public Result goSettlement(String[] ids,String userId){
        //先去redis中取出对应userId的map
        Map<String, TbItem> cart= cartFeign.getCartFromRedis(userId);
        //创建list
        List<TbItem> list = new ArrayList<>();
        //遍历用户勾选的商品id
        for (String id : ids) {
            list.add(cart.get(id));
        }
        if(list.size()>0){
            return Result.ok(list);
        }
        return Result.error("订单展示数据出错！！！");
    }
}
