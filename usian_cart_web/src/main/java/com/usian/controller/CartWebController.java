package com.usian.controller;
import com.bjsxt.utils.JsonUtils;
import com.bjsxt.utils.Result;
import com.netflix.discovery.converters.Auto;
import com.usian.feign.CartFeign;
import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.usian.utils.CookieUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/frontend/cart")
public class CartWebController {

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    @Value("${CART_COOKIE_EXPIRE}")
    private Integer CART_COOKIE_EXPIRE;

    @Autowired
    private ItemServiceFeign itemServiceFeign;

    @Autowired
    private CartFeign cartFeign;

    /**
     * 将商品加入购物车
     * @param userId 当userId存在的话说明存在用户 不存在则不存在用户
     * @param itemId 商品id
     * */
    @RequestMapping("/addItem")
    public Result addItem(String userId, Long itemId, @RequestParam(defaultValue = "1") Integer num,
                          HttpServletRequest request, HttpServletResponse response){
        try {
            if (StringUtils.isBlank(userId)){
                /***********未登录的情况下*************/
                //从cookie中查询购物车列表map
                Map<String, TbItem> cart = getCartForCookie(request);
                //添加商品到购物车
                addTbItemToCart(cart,itemId,num);
                //将购物车存到cookie
                addClientCookie(cart,request,response);
            }else{
                /***********登录的情况下*************/
                //先从redis中查出map,使用的hash
                Map<String,TbItem> map = getCartFromRedis(userId);

                //查出map后,根据itemId查出item
                TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
                //将查出的item加入到map中
                addTbItemToCart(map,itemId,num);
                //将添加后的map存到redis中
                boolean b = addCartToRedis(map,userId);
                if(!b){
                    return Result.error("添加购物车失败！！！");
                }
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("添加购物车失败！！！");
        }
    }

    //根据对应id从redis中查找出map
    private Map<String, TbItem> getCartFromRedis(String userId) {
        Map<String,TbItem> map = cartFeign.getCartFromRedis(userId);
        return map;
    }


    //传map和userId存到redis中
    private boolean addCartToRedis(Map<String, TbItem> map, String userId) {
        return cartFeign.addCartToRedis(map,userId);
    }


    //把购物车列表写到cookie
    private void addClientCookie(Map<String, TbItem> cart, HttpServletRequest request, HttpServletResponse response) {
        //将map转为json
        String cartJson = JsonUtils.objectToJson(cart);
        //将Map作为value,设置失效时间CART_COOKIE_EXPIRE,编码true,keyCART_COOKIE_KEY
        CookieUtils.setCookie(request,response,CART_COOKIE_KEY,cartJson,CART_COOKIE_EXPIRE,true);
    }

    //添加商品到购物车
    private void addTbItemToCart(Map<String, TbItem> cart, Long itemId, Integer num) {
        //先去数据库中查询要添加的商品
        TbItem tbItem = cart.get(itemId.toString());
        //判断tbItem是否为空
        if(tbItem!=null){
            //如果购物车中有该商品，数量就加一
            tbItem.setNum(tbItem.getNum()+num);
        }else{//如果购物车没有则新建
            tbItem = itemServiceFeign.selectItemInfo(itemId);
            tbItem.setNum(num);
        }
        cart.put(itemId.toString(),tbItem);
    }

    //查询商品列表
    private Map<String, TbItem> getCartForCookie(HttpServletRequest request) {
        //从cookie中查询出
        String cartJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);

        //判断是否为空
        if(StringUtils.isNotBlank(cartJson)){
            Map<String,TbItem> map = JsonUtils.jsonToMap(cartJson, TbItem.class);
            return map;
        }
        return new HashMap<String, TbItem>();
    }


    //查询购物车列表
    @RequestMapping("/showCart")
    public Result showCart(String userId,HttpServletRequest request){
        try {
            //创建list进行返回
            List<TbItem> tbItemList = new ArrayList<>();
            if(StringUtils.isBlank(userId)){ //在用户未登录的状态下
                //先从cookie中获取map集合
                Map<String, TbItem> cart = getCartForCookie(request);

                //遍历map，存到list
                Set<String> keys = cart.keySet();
                for (String key : keys) {
                    tbItemList.add(cart.get(key));
                }
            }else{// 在用户已登录的状态
                //根据userId获取到对应的map
                Map<String, TbItem> cart = cartFeign.getCartFromRedis(userId);
                for (String key : cart.keySet()) {
                    TbItem tbItem = cart.get(key);
                    tbItemList.add(tbItem);
                }
            }
            //返回前台
            return Result.ok(tbItemList);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("查询购物车列表失败！！！");
        }
    }

    //修改购物车数量
    @RequestMapping("/updateItemNum")
    public Result updateItemNum(String userId,Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response){
        try {
            // 在用户未登录的状态
            if(StringUtils.isBlank(userId)){
                //先从cookie中获取到map
                Map<String, TbItem> map = getCartForCookie(request);
                //根据itemId获取到商品信息
                TbItem tbItem = map.get(itemId.toString());
                //对num进行加1
                tbItem.setNum(num);
                //把修改后的map在存到cookie
                addClientCookie(map,request,response);
            }else{// 在用户已登录的状态
                //先获取到map
                Map<String, TbItem> cart = cartFeign.getCartFromRedis(userId);
                //根据itemId查找tbItem
                TbItem tbItem = cart.get(itemId.toString());
                //进行修改
                tbItem.setNum(num);
                //把修改后的值存到Redis中
                boolean b = cartFeign.addCartToRedis(cart, userId);
                if(!b){
                    return Result.error("增加商品数量出错！！！");
                }
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("增加商品数量出错！！！");
    }

    //删除商品
    @RequestMapping("/deleteItemFromCart")
    public Result deleteItemFromCart(String userId,Long itemId,HttpServletRequest request,HttpServletResponse response){
        try {
            if (StringUtils.isBlank(userId)){
                //先查询查现有的map
                Map<String, TbItem> cart = getCartForCookie(request);
                //去删除对应itemId商品
                cart.remove(itemId.toString());
                //将修改后的map添加到cookie
                addClientCookie(cart,request,response);
            }else{
                //先查询现有的map
                Map<String, TbItem> cart = cartFeign.getCartFromRedis(userId);
                //在map中删除对应itemId
                cart.remove(itemId.toString());
                //把删除完后的map重新再写到redis中
                boolean b = cartFeign.addCartToRedis(cart,userId);
                if(!b){
                    return Result.error("删除购物车商品出错！！！");
                }
            }
            return Result.ok();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("删除购物车商品出错！！！");
    }

}
