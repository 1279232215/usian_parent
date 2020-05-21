package com.usian.service;

import com.netflix.discovery.converters.Auto;
import com.usian.mapper.TbContentCategoryMapper;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentCategoryServiceImpI implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    //内容分类管理查询
    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(id);            //绑定条件parentId
        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        return tbContentCategoryList;
    }

    //内容分类管理添加
    @Override
    public int insertContentCategory(TbContentCategory tbContentCategory) {
        //先把添加的内容分类补充完
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setIsParent(false);
        tbContentCategory.setUpdated(new Date());
        tbContentCategory.setStatus(1);
        tbContentCategory.setSortOrder(1);
        //进行添加
        int num1 = tbContentCategoryMapper.insertSelective(tbContentCategory);
        //判断添加的新节点他的父节点is_parent是否为true
        TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
        //如果他的is_parent为false时，就把他改成true,他爹不是爹，就把他改成爹
        if(!contentCategory.getIsParent()){
            contentCategory.setIsParent(true);
            contentCategory.setUpdated(new Date());
            int num2 = tbContentCategoryMapper.updateByPrimaryKey(contentCategory);
            num1=num1+num2;
        }else{
            num1=num1+1;
        }

        return num1;
    }

    //内容分类管理删除
    @Override
    public int deleteContentCategoryById(Long categoryId) {

        //先判断一下传过来删除的节点是否有子节点
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        if(tbContentCategory.getIsParent())//当前节点有子节点
        {
            return 0;
        }
        //当前节点没有子节点
        tbContentCategoryMapper.deleteByPrimaryKey(categoryId);
        //删除完判断父节点还有没有子节点
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(tbContentCategory.getParentId());
        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        if(tbContentCategoryList.size()==0){
            TbContentCategory ContentCategory = new TbContentCategory();
            ContentCategory.setIsParent(false);
            ContentCategory.setUpdated(new Date());
            ContentCategory.setId(tbContentCategory.getParentId());
            tbContentCategoryMapper.updateByPrimaryKeySelective(ContentCategory);
        }
        return 200;
    }


    //内容分类管理修改,传过来的是当前id,和修改的name
    @Override
    public int updateContentCategory(Long id, String name) {
        TbContentCategory tbContentCategory = new TbContentCategory();
        tbContentCategory.setId(id);
        tbContentCategory.setName(name);
        tbContentCategory.setUpdated(new Date());
        int i = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
        return i;
    }
}
