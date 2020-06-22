package com.usian.service;

import com.usian.pojo.OrderInFo;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderItem;

import java.util.List;

public interface OrderService {
    String insertOrder(OrderInFo orderInFo);

    List<TbOrder> selectOverTbOrder();

    void updateTbOrder(TbOrder tbOrder);

    List<TbOrderItem> selectTbOrderItemByOrderId(String orderId);

    void addTbItemNum(TbOrderItem tbOrderItem);
}
