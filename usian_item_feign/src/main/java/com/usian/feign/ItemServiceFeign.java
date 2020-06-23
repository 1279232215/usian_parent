package com.usian.feign;

import com.usian.fallback.ItemServiceFallback;
import com.usian.pojo.*;
import com.usian.utils.CatResult;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "usian-item-service",fallbackFactory = ItemServiceFallback.class)
public interface ItemServiceFeign {

    //pojo可以不用@RequestBody,基本数据类型@RequestParam

    /**
     * 根据id查询商品基本信息
     * @param itemId
     * @return TbItem
     */
    @RequestMapping("/service/item/selectItemInfo")
    public TbItem selectItemInfo(@RequestParam Long itemId);

    /**
     * 分页查询TbItem商品数据
     *  @param page 当前页
     *  @param rows 当前页展示几条
     *  @return PageResult定义类
     */
    @RequestMapping("/service/item/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(@RequestParam Integer page,@RequestParam Long rows);

    /**
     *  查询商品类目ItemCat
     *  @param id parentId
     *  @return List<TbItemCat>
     */
    @RequestMapping("/service/itemCat/selectItemCategoryByParentId")
    List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id);

    /**
     *  根据itemCatId查询商品规格模板
     *  @param itemCatId 根据TbItemCatId对TbItemParam进行查询
     *  @return TbItemParam
     */
    @RequestMapping("/service/itemParam/selectItemParamByItemCatId/{itemCatId}")
    TbItemParam selectItemParamByItemCatId(@PathVariable Long itemCatId);

    /**
     *  @param tbItem     插入TbItem表
     *  @param desc       商品描述表
     *  @param itemParams 插入商品规格
     *  @return Integer
     */
    @RequestMapping("/service/item/insertTbItem")
    Integer insertTbItem(@RequestBody TbItem tbItem,@RequestParam String desc,@RequestParam String itemParams);

    /**
     *  根据itemId删除TbItem
     *  @param itemId id
     *  @return int
     */
    @RequestMapping("/service/item/deleteItemById")
    int deleteItemById(@RequestParam Long itemId);

    /**
     *  根据itemId删除TbItem
     *  @param itemId id
     *  @return int
     */
    @RequestMapping("/service/item/preUpdateItem")
    Map<String, Object> preUpdateItem(@RequestParam Long itemId);

    /**
     *  查询tbItem表,封装为pageResult
     *  @param page 当前页
     *  @param rows 每页展示多少条
     *  @return int
     */
    @RequestMapping("/service/itemParam/selectItemParamAll")
    PageResult selectItemParamAll(@RequestParam Integer page,@RequestParam Integer rows);

    /**
     *  根据主键id删除TbItemParam
     *  @param id
     *  @return int
     */
    @RequestMapping("/service/itemParam/deleteItemParamById")
    int deleteItemParamById(@RequestParam Long id);

    /**
     *  添加tbItemParam
     *  @param itemCatId
     *  @param paramData
     *  @return int
     */
    @RequestMapping("/service/itemParam/insertItemParam")
    int insertItemParam(@RequestParam Long itemCatId,@RequestParam String paramData);

    /**
     *  查询左侧商品分类目录
     *  @return CatResult 自定义util封装类
     */
    @RequestMapping("/service/itemCat/selectItemCategoryAll")
    CatResult selectItemCategoryAll();

    /**
     *  根据id查询商品基本参数
     *  @param itemId
     *  @return TbItemDesc
     */
    @RequestMapping("/service/item/selectItemDescByItemId")
    TbItemDesc selectItemDescByItemId(@RequestParam Long itemId);
    /**
     *  根据id查询商品规格参数
     *  @param itemId
     *  @return TbItemParamItem
     */
    @RequestMapping("/service/itemParam/selectTbItemParamItemByItemId")
    TbItemParamItem selectTbItemParamItemByItemId(@RequestParam Long itemId);
    /**
     *  根据id修改商品
     *  @param tbItem
     *  @param desc
     *  @param itemParams
     *  @return int
     */
    @RequestMapping("/service/item/updateTbItem")
    int updateTbItem(TbItem tbItem,@RequestParam String desc,@RequestParam String itemParams);
}
