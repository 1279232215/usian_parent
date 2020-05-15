package com.usian.feign;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "usian-item-service")
public interface ItemServiceFeign {
    /**
     * 根据id查询商品基本信息
     * @param itemId
     * @return
     */
    @RequestMapping("/service/item/selectItemInfo")
    public TbItem selectItemInfo(@RequestParam Long itemId);
    /**
     * 分页查询TbItem商品数据
     *  @param page 当前页
     *  @param rows 当前页展示几条
     *  @return
     */
    @RequestMapping("/service/item/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(@RequestParam Integer page,@RequestParam Long rows);
}
