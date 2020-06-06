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



    /*
    * 当商品删除之后监听队列
    * 先创建队列queue绑定到队列上
    * 根据routingKey获取传输的消息
    * */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue",declare = "true"),
            exchange = @Exchange(value = "item_exchange",type = ExchangeTypes.TOPIC),
            key = {"item.*"}
    ))
    public void deleteSynchronized(String msg){
        boolean b =  searchItemService.deleteSynchronized(msg);
        if(!b){
            throw new RuntimeException("商品删除同步失败！！！");
        }
    }
























}
