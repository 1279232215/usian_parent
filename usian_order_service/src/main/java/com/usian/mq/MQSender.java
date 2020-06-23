package com.usian.mq;

import com.alibaba.druid.support.json.JSONUtils;
import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import org.apache.tomcat.jni.Local;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import com.usian.utils.JsonUtils;
import org.springframework.stereotype.Component;

/*
* 任务
*   1、发送消息
*   2、消息确认成功返回后修改local_message(status:1)
*
* */
@Component
public class MQSender implements ReturnCallback,ConfirmCallback {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private LocalMessageMapper localMessageMapper;

    /*
    * 失败回调 ：消息发送消息失败时调用
    * */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText,
                                String exchange, String routingKey) {
        System.out.println("return--message:" + new String(message.getBody())
                + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }

    /*
    * 下有服务消息确认成功返回后调用
    * */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        String id = correlationData.getId();
        if(ack){
            //修改本地消息表的状态
            LocalMessage localMessage = new LocalMessage();
            localMessage.setState(1);
            localMessage.setTxNo(id);
            localMessageMapper.updateByPrimaryKey(localMessage);
        }
    }

    //提交订单，向itemService发送消息，完成扣减库存
    public void sendMsg(LocalMessage localMessage) {
        RabbitTemplate rabbitTemplate = (RabbitTemplate) this.amqpTemplate;
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);//确认回调
        rabbitTemplate.setReturnCallback(this);//失败回退
        //用于确认之后更改本地消息状态或删除本地消息--本地消息id
        CorrelationData correlationData = new CorrelationData(localMessage.getTxNo());
        //发送消息
        rabbitTemplate.convertAndSend("order_exchange","order.add", JsonUtils.objectToJson(localMessage),correlationData);
    }
}
