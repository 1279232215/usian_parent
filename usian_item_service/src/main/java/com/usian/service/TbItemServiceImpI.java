package com.usian.service;

import com.usian.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.netflix.discovery.converters.Auto;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bjsxt.utils.IDUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TbItemServiceImpI implements TbItemService{

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    //RabbitMq工具
    @Autowired
    private AmqpTemplate amqpTemplate;

    //redis工具
    @Autowired
    private RedisClient redisClient;

    //setnx()描述信息
    @Value("${SETNX_DESC_LOCK_KEY}")
    private String SETNX_DESC_LOCK_KEY;

    //setnx()商品基本信息
    @Value("${SETNX_BASC_LOCK_KEY}")
    private String SETNX_BASC_LOCK_KEY;

    //商品基本信息
    @Value("${BASE}")
    private String BASE;

    //商品描述信息
    @Value("${DESC}")
    private String DESC;

    //商品规格信息
    @Value("${PARAM}")
    private String PARAM;

    //商品名称
    @Value("${ITEM_INFO}")
    private String ITEM_INFO;

    //失效时间
    @Value("${ITEM_INFO_EXPIRE}")
    private Long ITEM_INFO_EXPIRE;

    //根据id查询商品基本信息
    @Override
    public TbItem selectItemInf(Long itemId) {
        String key = ITEM_INFO+":"+itemId+":"+BASE;   //定义商品基本信息的key
        TbItem RedisTbItem = (TbItem)redisClient.get(key); //从redis中跟距key获取商品基本信息
        if(RedisTbItem!=null){                          //判断redis是否有基本信息
            return RedisTbItem;
        }
        /********       解决缓存击穿             ***********/
        if(redisClient.setnx(SETNX_BASC_LOCK_KEY+":"+itemId,itemId,30L)){ //setnx()如果你set的name不存在返回true存在则false
            TbItem mySqlTbItem = tbItemMapper.selectByPrimaryKey(itemId);//从mysql中查询商品基本信息
            if(mySqlTbItem!=null){
                redisClient.set(key,mySqlTbItem);   //赋值到redis
                redisClient.expire(key,ITEM_INFO_EXPIRE);//设置过期时间
            }else{
                /********       解决缓存穿透        如果mysql也没有值的话     ***********/
                redisClient.set(key,null);  //设置为null
                redisClient.expire(key,30L);//过期时间30
            }
            redisClient.del(SETNX_BASC_LOCK_KEY+":"+itemId); //删除击穿存的name
            return mySqlTbItem;   //返回商品基本信息
        }else{
            try {
                Thread.sleep(1000); //不是第一个用户，等待一秒
            }catch (Exception e){
                e.printStackTrace();
            }
            return selectItemInf(itemId); //继续访问
        }
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
        tbItemDesc.setItemDesc(desc);
        int tbItemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);
        //添加TbItemParam数据
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setUpdated(date);
        System.out.println(tbItemParamItem.getParamData());
        System.out.println(tbItemParamItem+"+++++++++++++++++++++++++++++++++++++");
        int tbItemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);


        //添加之后向usian-search-service发送同步索引消息
        amqpTemplate.convertAndSend("item_exchange","item.add",itemId);



        return tbItemNum+tbItemDescNum+tbItemParamItemNum;//返回3条insert的执行影响条数
    }

    //根据itemId删除TbItem
    @Override
    public int deleteItemById(Long itemId) {
        //deleteByPrimaryKey()根据id删除
        //deleteByExample()根据条件删除
        amqpTemplate.convertAndSend("item_exchange","item.delete",itemId);
        //调用redis同步方法
        redisSynchronized(itemId);
        return tbItemMapper.deleteByPrimaryKey(itemId);
    }


    @Override
    public Map<String, Object> preUpdateItem(Long itemId) {
        //定义map集合
        Map<String,Object> map = new HashMap<>();
        //查询tbItem表
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        //将tbItem存入Map集合
        map.put("item",tbItem);

        //根据商品ID查询商品描述
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        //将TbItemDesc存入map中
        map.put("itemDesc",tbItemDesc.getItemDesc());

        //根据商品ID查询商品类目
        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbItem.getCid());
        //将itemCat存入map
        map.put("itemCat",tbItemCat.getName());

        //根据商品ID查询TbItemParamItem
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        map.put("itemParamItem",tbItemParamItems.get(0).getParamData());
        return map;
    }
    //根据商品id查询TbItemDesc中数据
    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {
        String key = ITEM_INFO+":"+itemId+":"+DESC;
        //查询缓存
        TbItemDesc tbItemDesc = (TbItemDesc)redisClient.get(key);
        if(tbItemDesc!=null){
            return tbItemDesc;
        }
/********************解决缓存击穿************************/
        if (redisClient.setnx(SETNX_DESC_LOCK_KEY+":"+itemId,itemId,30L)){
            TbItemDescExample tbItemDescExample = new TbItemDescExample();
            TbItemDescExample.Criteria criteria = tbItemDescExample.createCriteria();
            criteria.andItemIdEqualTo(itemId);
            List<TbItemDesc> tbItemDescList = tbItemDescMapper.selectByExampleWithBLOBs(tbItemDescExample);
            if(tbItemDescList!=null && tbItemDescList.size()>0){
                //把数据保存到缓存
                redisClient.set(key,tbItemDescList.get(0));
                redisClient.expire(key,ITEM_INFO_EXPIRE);
            }else{
                //解决缓存穿透
                //把空对象保存到缓存
                redisClient.set(key,null);
                redisClient.expire(key,30L);
            }
            redisClient.del(SETNX_DESC_LOCK_KEY+":"+itemId);
            return  tbItemDescList.get(0);
        }else{
            try {
                //等待一秒
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            return selectItemDescByItemId(itemId);
        }

    }


    //修改商品
    @Override
    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        Date date = new Date();               //new 一个当前时间
        tbItem.setUpdated(date);
        int tbItemNum = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        //修改商品TbItemDesc数据
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(tbItem.getId());
        tbItemDesc.setUpdated(date);
        tbItemDesc.setItemDesc(desc);
        int tbItemDescNum = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
        //修改商品TbItemParam数据
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(tbItem.getId());
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        int tbItemParamItemNum=0;
        if(tbItemParamItems!=null && tbItemParamItems.size()>0){
            TbItemParamItem tbItemParamItem = tbItemParamItems.get(0);
            tbItemParamItem.setParamData(itemParams);
            tbItemParamItem.setItemId(tbItem.getId());
            tbItemParamItem.setUpdated(date);
            tbItemParamItemNum = tbItemParamItemMapper.updateByExampleWithBLOBs(tbItemParamItem,tbItemParamItemExample);
        }

        //调用redis同步方法
        redisSynchronized(tbItem.getId());
        return tbItemNum+tbItemDescNum+tbItemParamItemNum;//返回3条insert的执行影响条数
    }


    //结算购物车商品，对数据库的商品库存进行扣除
    @Override
    public Integer updateTbItemByOrderId(String orderId) {
        //先根据orderId查询出结算的商品
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);
        //遍历结算的商品
        int result = 0;
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            //获取到商品的id
            String itemId = tbOrderItem.getItemId();
            //根据itemId查询出商品信息
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(itemId));
            System.out.println(tbItem);
            //对查询出的商品进行扣减
            System.out.println(tbItem.getNum());System.out.println(tbOrderItem.getNum());
            tbItem.setNum(tbItem.getNum()-tbOrderItem.getNum());
            //扣减之后调用方法进行修改tbItem的值
            result += tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
        return result;
    }


    public void redisSynchronized(Long itemId){
        redisClient.del(ITEM_INFO+":"+itemId+":"+BASE);
        redisClient.del(ITEM_INFO+":"+itemId+":"+DESC);
        redisClient.del(ITEM_INFO+":"+itemId+":"+ PARAM);
    }
}
