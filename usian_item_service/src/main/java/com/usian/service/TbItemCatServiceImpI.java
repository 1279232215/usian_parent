package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    //查询前台左侧商品分类目录
    @Override
    public CatResult selectItemCategoryAll() {
        CatResult catResult = new CatResult();//创建自定义返回类
        catResult.setData(getCatList(0L));//调用自定义方法进行赋值,传的是父类parentId=0
        return catResult;                     //进行返回
    }

    public List getCatList(Long parentId){
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> tbItemCatList = tbItemCatMapper.selectByExample(tbItemCatExample);
        List catNodeList = new ArrayList();
        int count=0;
        for (TbItemCat t:
                tbItemCatList) {
            if(t.getIsParent()){//是父节点
                CatNode catNode = new CatNode();
                catNode.setName(t.getName());
                catNode.setItem(getCatList(t.getId()));
                catNodeList.add(catNode);
                count++;
                if(count==18){//前台就要18行
                    break;
                }
            }else{
                catNodeList.add(t.getName());
            }
        }
        return catNodeList;
    }
}
