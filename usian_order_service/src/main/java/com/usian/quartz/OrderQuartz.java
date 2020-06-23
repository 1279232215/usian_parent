package com.usian.quartz;


import ch.qos.logback.core.pattern.color.RedCompositeConverter;
import com.usian.mapper.LocalMessageMapper;
import com.usian.mq.MQSender;
import com.usian.pojo.LocalMessage;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderItem;
import com.usian.redis.RedisClient;
import com.usian.service.LocalMessageService;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

public class OrderQuartz implements Job {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private LocalMessageService localMessageService;

    @Autowired
    private MQSender mqSender;
    //关闭超时订单
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String ip;
        try {
            //获取访问主机的ip
            ip = InetAddress.getLocalHost().getHostAddress();
            if(redisClient.setnx("SETNX_LOCK_KEY",ip,30L)){
                System.out.println("执行关闭超时订单任务...."+new Date());
                //1、查询超时订单
                List<TbOrder> tbOrderList = orderService.selectOverTbOrder();
                //2、关闭超时订单
                for (TbOrder tbOrder : tbOrderList) {
                    orderService.updateTbOrder(tbOrder);
                    //3、关闭订单后对对应的商品数量增加
                    //3.1先跟据订单id查询出订单中的商品
                    List<TbOrderItem> tbOrderItemList = orderService.selectTbOrderItemByOrderId(tbOrder.getOrderId());
                    //3.2遍历所有的商品
                    for (TbOrderItem tbOrderItem : tbOrderItemList) {
                        orderService.addTbItemNum(tbOrderItem);
                    }
                }
                redisClient.del("SETNX_LOCK_KEY");
                System.out.println("执行扫描本地消息表的任务...." + new Date());


                //利用定时器查看本地消息没有发送成功的
                List<LocalMessage> localMessageList =
                        localMessageService.selectlocalMessageByStatus(0);
                for (LocalMessage localMessage : localMessageList) {
                    mqSender.sendMsg(localMessage);
                }

            }else{
                System.out.println("============机器："+ip+" 占用分布式锁，任务正在执行=======================");
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
