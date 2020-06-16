package com.usian.controller;
import com.bjsxt.utils.Result;
import com.usian.feign.CartFeign;
import com.usian.feign.SSOFeign;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbUser;
import com.usian.utils.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bjsxt.utils.JsonUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {

    @Autowired
    private SSOFeign ssoFeign;

    @Autowired
    private CartFeign cartFeign;

    @Value("${CART_COOKIE_KEY}")
    private String CART_COOKIE_KEY;

    //用户注册验证手机号，和用户名不重复,
    //当checkFlag是1的话checkValue是用户名     当checkFlag是2的话checkValue是手机号
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public Result checkUserInfo(@PathVariable String checkValue,@PathVariable Integer checkFlag){
        boolean b = ssoFeign.checkUserInfo(checkValue,checkFlag);
        if(b){
            return  Result.ok();
        }
        return Result.error("数据(用户名||手机号)重复！！！");
    }

    //用户注册
    @RequestMapping("/userRegister")
    public Result userRegister(TbUser tbUser){
        int i = ssoFeign.userRegister(tbUser);
        if(i==1){
            return Result.ok();
        }
        return Result.error("用户注册失败！！！");
    }

    //用户登录
    @RequestMapping("/userLogin")
    public Result userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response){
        //去登陆
        Map map = ssoFeign.userLogin(username,password);

        if(map!=null){
            //登录先查询一下cookie中是否存有数据
            String cartJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY, true);
            if(StringUtils.isNotBlank(cartJson)){
                Map<String,TbItem> cookieCart = JsonUtils.jsonToMap(cartJson,TbItem.class);
                //根据登录之后返回的参数获取到userId
                String userId =(String) map.get("userid");
                //再把用户中redis中的map取出来
                Map<String, TbItem> redisCart = cartFeign.getCartFromRedis(userId);
                //遍历cookieMap
                Set<String> keys = cookieCart.keySet();
                //遍历所有的key
                for (String key : keys) {
                    TbItem cookieTbItem = cookieCart.get(key);
                    TbItem redisTbItem = redisCart.get(key);
                    if(redisTbItem==null){
                        redisCart.put(key,cookieTbItem);
                    }else{
                        redisTbItem.setNum(redisTbItem.getNum()+cookieTbItem.getNum());
                        redisCart.put(key,redisTbItem);
                    }
                }
                //修改完毕，进行向redis覆盖
                cartFeign.addCartToRedis(redisCart,userId);
                //覆盖完毕把cookie中数据清空
                CookieUtils.deleteCookie(request,response,CART_COOKIE_KEY);
            }
            return Result.ok(map);
        }
        return  Result.error("用户登录失败！！！");
    }

    //通过token查询用户信息,查询用户登录是否过期
    @RequestMapping("/getUserByToken/{token}")
    public Result getUserByToken(@PathVariable String token){
        TbUser tbUser = ssoFeign.getUserByToken(token);
        if(tbUser!=null){
            return Result.ok();
        }
        return Result.error("Redis中没有该用户信息！！！");
    }

    //退出登录
    @RequestMapping("/logOut")
    public Result logOut(String token){
        boolean b  = ssoFeign.logOut(token);
        if(b){
            return Result.ok();
        }
        return Result.error("退出登录失败！！！");
    }
}
