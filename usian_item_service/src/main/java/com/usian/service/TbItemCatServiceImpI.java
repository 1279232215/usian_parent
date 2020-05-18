package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TbItemCatServiceImpI implements TbItemCatService{
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    //根据parent查询商品类目
    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample(); //创建逆向工程的工具类
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();//创建where
        criteria.andParentIdEqualTo(id);//判断parentId
        criteria.andStatusEqualTo(1);//没有下架的商品
        List<TbItemCat> tbItemCats = tbItemCatMapper.selectByExample(tbItemCatExample);
        return tbItemCats;
    }
}
