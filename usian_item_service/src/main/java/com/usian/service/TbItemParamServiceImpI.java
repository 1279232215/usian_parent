package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.pojo.TbItemParamItem;
import com.usian.pojo.TbItemParamItemExample;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Service
@Transactional
public class TbItemParamServiceImpI implements TbItemParamService {
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;
    @Autowired
    private RedisClient redisClient;


    @Value("${PARAM}")
    private String PARAM;

    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    @Value("${ITEM_INFO_EXPIRE}")
    private Long ITEM_INFO_EXPIRE;

    @Value("${SETNX_PARAM_LOCK_KEY}")
    private String SETNX_PARAM_LOCK_KEY;

    //查询商品规格模板
    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId); //设置条件
        List<TbItemParam> itemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);//因为selectByExample()默认描述是个大容量默认不查询，BlOBs是可以查描述了
        if(itemParams!=null && itemParams.size()>0){
            return itemParams.get(0);
        }
        return null;
    }


    //分页查询商品模板
    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        tbItemParamExample.setOrderByClause("updated DESC");
        PageHelper.startPage(page,rows);
        List<TbItemParam> itemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        PageInfo<TbItemParam> pageInfo = new PageInfo<TbItemParam>(itemParams);
        PageResult pageResult = new PageResult();
        pageResult.setResult(pageInfo.getList());
        pageResult.setTotalPage(pageInfo.getTotal());
        pageResult.setPageIndex(pageInfo.getPageNum());
        return pageResult;
    }

    //添加商品规格模板
    @Override
    public int insertItemParam(Long itemCatId, String paramData) {
        //先判断添加itemCatId是否已经存在对应模板
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> itemParamsList = tbItemParamMapper.selectByExample(tbItemParamExample);
        if(itemParamsList.size()>0){
            return 0;
        }
        TbItemParam tbItemParam = new TbItemParam();      //创建添加TbItemParam进行赋值
        Date date = new Date();                           //new 一个当前时间
        tbItemParam.setItemCatId(itemCatId);              //赋值
        tbItemParam.setParamData(paramData);
        tbItemParam.setCreated(date);
        tbItemParam.setUpdated(date);
        int i = tbItemParamMapper.insertSelective(tbItemParam);//添加
        return i;
    }

    //根据主键id删除商品规格模板tb_item_param
    @Override
    public int deleteItemParamById(Long id) {
        int i = tbItemParamMapper.deleteByPrimaryKey(id);
        return i;
    }


    //根据商品id查询商品规格参数
    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        String key = ITEM_INFO+":"+itemId+":"+PARAM;
        //查询缓存
        TbItemParamItem redisTbItemParamItem = (TbItemParamItem)redisClient.get(key);
        if(redisTbItemParamItem!=null){
            return redisTbItemParamItem;
        }
        /********************解决缓存击穿************************/
        if(redisClient.setnx(SETNX_PARAM_LOCK_KEY+":"+itemId,itemId,30L)){
            TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
            TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
            criteria.andItemIdEqualTo(itemId);
            List<TbItemParamItem> tbItemParamItemList = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
            if(tbItemParamItemList!=null && tbItemParamItemList.size()>0){
                //把数据保存到缓存
                redisClient.set(key,tbItemParamItemList.get(0));
                redisClient.expire(key,ITEM_INFO_EXPIRE);
            }else{
                //解决缓存穿透
                //把空对象保存到缓存
                redisClient.set(key,null);
                redisClient.expire(key,30L);
            }
            redisClient.del(SETNX_PARAM_LOCK_KEY+":"+itemId);
            return tbItemParamItemList.get(0);
        }else{
            try {
                //等待一秒
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            //继续访问
            return selectTbItemParamItemByItemId(itemId);
        }
    }
}
