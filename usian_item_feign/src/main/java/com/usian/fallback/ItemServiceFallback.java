package com.usian.fallback;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.*;
import com.usian.utils.CatResult;
import com.usian.utils.PageResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * item 服务返回托底数据
 */
@Component
public class ItemServiceFallback implements FallbackFactory<ItemServiceFeign>{


    @Override
    public ItemServiceFeign create(Throwable cause) {
        return new ItemServiceFeign() {
            @Override
            public TbItem selectItemInfo(Long itemId) {
                return null;
            }

            @Override
            public PageResult selectTbItemAllByPage(Integer page, Long rows) {
                return null;
            }

            @Override
            public List<TbItemCat> selectItemCategoryByParentId(Long id) {
                return null;
            }

            @Override
            public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
                return null;
            }

            @Override
            public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
                return null;
            }

            @Override
            public int deleteItemById(Long itemId) {
                return 0;
            }

            @Override
            public Map<String, Object> preUpdateItem(Long itemId) {
                return null;
            }

            @Override
            public PageResult selectItemParamAll(Integer page, Integer rows) {
                return null;
            }

            @Override
            public int deleteItemParamById(Long id) {
                return 0;
            }

            @Override
            public int insertItemParam(Long itemCatId, String paramData) {
                return 0;
            }

            @Override
            public CatResult selectItemCategoryAll() {
                return null;
            }

            @Override
            public TbItemDesc selectItemDescByItemId(Long itemId) {
                return null;
            }

            @Override
            public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
                return null;
            }

            @Override
            public int updateTbItem(TbItem tbItem, String desc, String itemParams) {
                return 0;
            }
        };
    }
}