package com.usian.service;

import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbOrderItemMapper;
import com.usian.mapper.TbOrderMapper;
import com.usian.mapper.TbOrderShippingMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bjsxt.utils.JsonUtils;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpI implements OrderService {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Long ORDER_ID_BEGIN;

    @Value("${ORDER_ITEM_ID_KEY}")
    private String ORDER_ITEM_ID_KEY;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisClient redisClient;

    /*
     * 提交订单
     * 创建订单
     * */
    @Override
    public String insertOrder(OrderInFo orderInFo) {
        //获取到自定义类封装的接受的对象
        //订单商品的信息
        List<TbOrderItem> tbOrderItemList = JsonUtils.jsonToList(orderInFo.getOrderItem(), TbOrderItem.class);
        //订单信息
        TbOrder tbOrder = orderInFo.getTbOrder();
        //订单用户信息
        TbOrderShipping tbOrderShipping = orderInFo.getTbOrderShipping();
        //创建当前时间
        Date date = new Date();
        Long orderId = ORDER_ID_BEGIN;
        //**************1、向订单插入信息********************//
        if(!redisClient.exists(ORDER_ID_KEY)){          //如果没有存在对应的订单id
            redisClient.set(ORDER_ID_KEY,ORDER_ID_BEGIN); //则存到redis对应的订单号
        }else{
            //如果存在对应的订单id则自增长
            orderId = redisClient.incr(ORDER_ID_KEY, 1L);
        }
        //对tbOrder进行赋值
        tbOrder.setOrderId(orderId.toString());
        tbOrder.setPostFee("0");//邮费
        tbOrder.setCreateTime(date);
        tbOrder.setUpdateTime(date);
        //1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        tbOrder.setStatus(1);
        tbOrderMapper.insertSelective(tbOrder);
        //**************2、向订单明细表插入数据********************//
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            tbOrderItem.setId(redisClient.incr(ORDER_ITEM_ID_KEY, 1L)+"");
            tbOrderItem.setOrderId(orderId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }
        //**************3、保存物流信息********************//
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShipping.setCreated(date);
        tbOrderShipping.setUpdated(date);
        tbOrderShippingMapper.insertSelective(tbOrderShipping);
        //发布消息到消息队列，完成扣减库存
        amqpTemplate.convertAndSend("order_exchange","order.add",orderId);
        return orderId.toString();
    }


    //查询出超时的订单
    @Override
    public List<TbOrder> selectOverTbOrder() {
        return tbOrderMapper.selectOverTbOrder();
    }


    //修改超时的订单
    @Override
    public void updateTbOrder(TbOrder tbOrder) {
        Date date = new Date();
        tbOrder.setStatus(6);
        tbOrder.setUpdateTime(date);
        tbOrder.setEndTime(date);
        tbOrder.setCloseTime(date);
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }


    //根据订单id查询出该订单有多少商品
    @Override
    public List<TbOrderItem> selectTbOrderItemByOrderId(String orderId) {
        //创建TbOrderItem查询条件
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);
        return tbOrderItemList;
    }


    //根据订单中的商品信息，对商品表进行增加库存修改
    @Override
    public void addTbItemNum(TbOrderItem tbOrderItem) {
        //先根据tbOrderItem的商品id查询出对应的商品信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
        //然后进行修改tbItem
        tbItem.setNum(tbItem.getNum()+tbOrderItem.getNum());
        tbItem.setUpdated(new Date());
        //进行修改
        tbItemMapper.updateByPrimaryKeySelective(tbItem);
    }
}
