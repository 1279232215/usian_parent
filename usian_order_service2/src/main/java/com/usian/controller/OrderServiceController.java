package com.usian.controller;

import com.usian.pojo.OrderInFo;
import com.usian.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/order")
public class OrderServiceController {

    @Autowired
    private OrderService orderService;
    /*
     * 提交订单
     * 创建订单
     * */
    @RequestMapping("/insertOrder")
    public String insertOrder(@RequestBody OrderInFo orderInFo){
        return orderService.insertOrder(orderInFo);
    }

}
