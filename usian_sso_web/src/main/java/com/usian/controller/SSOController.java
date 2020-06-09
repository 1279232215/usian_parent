package com.usian.controller;
import com.bjsxt.utils.Result;
import com.usian.feign.SSOFeign;
import com.usian.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/frontend/sso")
public class SSOController {

    @Autowired
    private SSOFeign ssoFeign;

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
    public Result userLogin(String username,String password){
        Map map = ssoFeign.userLogin(username,password);
        if(map!=null){
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
        System.out.println(token+"================");
        boolean b  = ssoFeign.logOut(token);
        if(b){
            return Result.ok();
        }
        return Result.error("退出登录失败！！！");
    }
}
