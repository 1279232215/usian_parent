package com.usian.feign;

import com.usian.pojo.OrderInFo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("usian-order-service")
public interface OrderServiceFeign {
    /*
     * 提交订单
     * 创建订单
     * */
    @RequestMapping("/service/order/insertOrder")
    String insertOrder(OrderInFo orderInFo);
}
