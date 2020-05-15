package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemMapper;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TbItemServiceImpI implements TbItemService{

    @Autowired
    private TbItemMapper tbItemMapper;//mapper接口
    /**
     * 根据id查询商品基本信息
     * @param itemId
     * @return
     */
    @Override
    public TbItem selectItemInf(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
    }
    /**
     * 分页查询TbItem商品数据
     *  @param page
     *  @param rows
     *  @return
     */
    @Override
    public PageResult selectTbItemAllByPage(Integer page, Long rows) {
        PageHelper.startPage(page,rows.intValue());  //设置分页查询数据
        TbItemExample example = new TbItemExample(); //创建逆向工程生成的实体类的sql工具类
        TbItemExample.Criteria criteria = example.createCriteria();//创建类的条件，类似于创建一个where，让你添加条件
        criteria.andStatusEqualTo((byte)1);//创建条件是 and status(字段) = 1
        List<TbItem> itemList = tbItemMapper.selectByExample(example);//逆向工程生成的方法
        PageInfo<TbItem> pageInfo = new PageInfo<>();       //分页
        PageResult pageResult = new PageResult();           //创建接口文档返回的类
        pageResult.setPageIndex(pageInfo.getPageNum());     //使用pageinfo的参数注入到返回类中
        pageResult.setTotalPage(pageInfo.getTotal());//使用pageinfo的参数注入到返回类中
        pageResult.setResult(itemList);//使用pageinfo的参数注入到返回类中
        return pageResult;//返回参数
    }
}
