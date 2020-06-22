package com.usian.pojo;

public class OrderInFo {
    private String orderItem;
    private TbOrder tbOrder;
    private TbOrderShipping tbOrderShipping;

    public OrderInFo() {
    }

    public OrderInFo(String orderItem, TbOrder tbOrder, TbOrderShipping tbOrderShipping) {
        this.orderItem = orderItem;
        this.tbOrder = tbOrder;
        this.tbOrderShipping = tbOrderShipping;
    }

    public String getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public TbOrderShipping getTbOrderShipping() {
        return tbOrderShipping;
    }

    public void setTbOrderShipping(TbOrderShipping tbOrderShipping) {
        this.tbOrderShipping = tbOrderShipping;
    }
}
