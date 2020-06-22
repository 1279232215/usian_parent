package com.usian.listener;

import com.usian.ItemServiceApp;
import com.usian.service.TbItemService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemMQListener {

    @Autowired
    private TbItemService tbItemService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "item_queue",declare="true"),
            exchange = @Exchange(value = "order_exchange",type = ExchangeTypes.TOPIC),
            key = {"order.*"}
    ))
    public void listen(String orderId){
        Integer result = tbItemService.updateTbItemByOrderId(orderId);
        if(!(result>0)){
            throw new RuntimeException("订单结算后，对商品进行回扣!!!");
        }
    }
}
