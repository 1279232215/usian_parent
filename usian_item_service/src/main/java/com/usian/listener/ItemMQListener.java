package com.usian.listener;
import com.rabbitmq.client.Channel;
import com.usian.pojo.DeDuplication;
import com.usian.pojo.LocalMessage;
import com.usian.service.DeDuplicationService;
import com.usian.service.TbItemService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.usian.utils.JsonUtils;

import java.util.Date;

@Component
public class ItemMQListener {

    @Autowired
    private TbItemService tbItemService;

    @Autowired
    private DeDuplicationService deDuplicationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "item_queue",declare="true"),
            exchange = @Exchange(value = "order_exchange",type = ExchangeTypes.TOPIC),
            key = {"order.*"}
    ))
    public void listen(String msg, Channel channel, Message message){
        try {
            //将传过来的LocalMessage的json传转为LocalMessage对象
            LocalMessage localMessage = JsonUtils.jsonToPojo(msg, LocalMessage.class);
            //查询消息去重表
            DeDuplication deDuplication = deDuplicationService.selectDeDuplicationByTxNo(localMessage.getTxNo());
            if(deDuplication==null){//如果为空说明没有执行业务
                int a = 6/0;
                Integer result = tbItemService.updateTbItemByOrderId(localMessage.getOrderNo());
                if(!(result>0)){
                    throw new RuntimeException("订单结算后，对商品进行回扣!!!");
                }
                //没问题之后设置去重表说自己已完成此业务
                deDuplicationService.insertDeDuplication(localMessage.getTxNo());
            }else {
                System.out.println("=======幂等生效：事务"+deDuplication.getTxNo()
                        +" 已成功执行===========");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
