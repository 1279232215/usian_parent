package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemExample;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.bjsxt.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TbItemServiceImpI implements TbItemService{

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    //根据id查询商品基本信息
    @Override
    public TbItem selectItemInf(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

    //分页查询TbItem商品数据
    @Override
    public PageResult selectTbItemAllByPage(Integer page, Long rows) {
        PageHelper.startPage(page,rows.intValue());  //设置分页查询数据
        TbItemExample example = new TbItemExample(); //创建逆向工程生成的实体类的sql工具类
        TbItemExample.Criteria criteria = example.createCriteria();//创建类的条件，类似于创建一个where，让你添加条件
        criteria.andStatusEqualTo((byte)1);//创建条件是 and status(字段) = 1
        example.setOrderByClause("created DESC");
        List<TbItem> itemList = tbItemMapper.selectByExample(example);//逆向工程生成的方法
        PageInfo<TbItem> pageInfo = new PageInfo<>();       //分页
        PageResult pageResult = new PageResult();           //创建接口文档返回的类
        pageResult.setPageIndex(pageInfo.getPageNum());     //使用pageinfo的参数注入到返回类中
        pageResult.setTotalPage(pageInfo.getTotal());//使用pageinfo的参数注入到返回类中
        pageResult.setResult(itemList);//使用pageinfo的参数注入到返回类中
        return pageResult;//返回参数
    }

    //添加TbItem-商品表,TbItemDesc-商品描述,TbItemParams-商品规格
    @Override
    public Integer insertTbItem(@RequestBody TbItem tbItem,String desc,String itemParams) {
        long itemId = IDUtils.genItemId();    //随机获取id,作为ItemId
        Date date = new Date();               //new 一个当前时间
        //添加TbItem数据
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setUpdated(date);
        tbItem.setCreated(date);
        //但是如果使用inserSelective就会只给有值的字段赋值（会对传进来的值做非空判断）
        //如果选择insert 那么所有的字段都会添加一遍，即使有的字段没有值
        int tbItemNum = tbItemMapper.insertSelective(tbItem);
        //添加TbItemDesc数据
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setCreated(date);
        int tbItemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);
        //添加TbItemParam数据
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setId(itemId);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setUpdated(date);
        int tbItemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);
        return tbItemNum+tbItemDescNum+tbItemParamItemNum;//返回3条insert的执行影响条数
    }

    //根据itemId删除TbItem
    @Override
    public int deleteItemById(Long itemId) {
        //deleteByPrimaryKey()根据id删除
        //deleteByExample()根据条件删除
        return tbItemMapper.deleteByPrimaryKey(itemId);
    }
}
