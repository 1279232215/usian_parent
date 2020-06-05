package com.usian.listener;

import com.usian.service.SearchItemService;
import org.elasticsearch.search.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchListener {

    @Autowired
    private SearchItemService searchItemService;

    /**
     * 监听者接收消息三要素：
     *  1、queue
     *  2、exchange
     *  3、routing key
     */
    @RabbitListener(bindings = @QueueBinding(
            value =   @Queue(value="search_queue",declare="true"),
            exchange = @Exchange(value = "item_exchange",type = ExchangeTypes.TOPIC),
            key = {"item.*"}
    ))
    public void listener(String msg){
        boolean b = searchItemService.insertDocument(msg);
        if(!b){
            throw new RuntimeException("同步失败");
        }
    }

}
