package com.usian.controller;

import com.usian.utils.Result;
import com.usian.feign.CartFeign;
//import com.usian.feign.OrderServiceFeign;
import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.OrderInFo;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderShipping;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private OrderServiceFeign orderServiceFeign;


    /*
    * 提交订单
    * 创建订单
    * */
    @RequestMapping("/insertOrder")
    public Result insertOrder(String orderItem, TbOrder tbOrder, TbOrderShipping tbOrderShipping){
        //因为一个request(请求)只有一个request body 所以feign不支持多个@RequestBody
        OrderInFo orderInFo = new OrderInFo();
        //把接受的的对象，赋值到自定义的对象
        orderInFo.setOrderItem(orderItem);
        orderInFo.setTbOrder(tbOrder);
        orderInFo.setTbOrderShipping(tbOrderShipping);
        //调用feign，根据前台要的参数进行返回
        String orderId = orderServiceFeign.insertOrder(orderInFo);
        if(StringUtils.isNotBlank(orderId)){
            return Result.ok(orderId);
        }
        return Result.error("提交订单失败！！！");
    }


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
