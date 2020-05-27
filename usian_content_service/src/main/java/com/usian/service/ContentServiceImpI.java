package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.redis.RedisClient;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentServiceImpI implements ContentService {

    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Value("${portal_ad_redis_key}")
    private String portal_ad_redis_key;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TbContentMapper tbContentMapper;

    @Override
    public PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId) {
        PageHelper.startPage(page,rows);
        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        tbContentExample.setOrderByClause("updated DESC");
        List<TbContent> tbContents = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);
        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContents);
        PageResult pageResult = new PageResult();
        pageResult.setPageIndex(pageInfo.getPageNum());
        pageResult.setTotalPage((long)pageInfo.getPages());
        pageResult.setResult(pageInfo.getList());
        return pageResult;
    }

    //内容分类添加
    @Override
    public int insertTbContent(TbContent tbContent) {
        Date date = new Date();
        tbContent.setUpdated(date);
        tbContent.setCreated(date);
        int i = tbContentMapper.insertSelective(tbContent);
        redisClient.hdel(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        return i;
    }

    //内容分类删除
    @Override
    public int deleteContentByIds(Long id) {
        redisClient.hdel(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        return tbContentMapper.deleteByPrimaryKey(id);
    }

    //前台首页大广告查询
    @Override
    public List<AdNode> selectFrontendContentByAD() {
        //先去redis缓存中查询List<AdNode>
        List<AdNode> redisAdNodeList = (List<AdNode>) redisClient.hget(portal_ad_redis_key,AD_CATEGORY_ID.toString());
        if(redisAdNodeList!=null && redisAdNodeList.size()>0){
            return redisAdNodeList;
        }
        TbContentExample tbContentExample = new TbContentExample();
        TbContentExample.Criteria criteria = tbContentExample.createCriteria();
        criteria.andCategoryIdEqualTo(AD_CATEGORY_ID);
        List<TbContent> tbContentList = tbContentMapper.selectByExample(tbContentExample);
        List<AdNode> adNodeList = new ArrayList<>();
        for (TbContent t:
                tbContentList) {
            AdNode adNode = new AdNode();
            adNode.setHeight(AD_HEIGHT);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setHref(t.getUrl());
            adNode.setSrc(t.getPic());
            adNode.setSrcB(t.getPic2());
            adNode.setWidth(AD_WIDTH);
            adNode.setWidthB(AD_WIDTHB);
            adNodeList.add(adNode);
        }
        //上面if没有生效就走数据库，查到数据添加到缓存
        redisClient.hset(portal_ad_redis_key,AD_CATEGORY_ID.toString(),adNodeList);
        return adNodeList;
    }
}
