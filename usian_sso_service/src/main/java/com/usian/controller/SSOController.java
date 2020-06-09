package com.usian.controller;

import com.usian.pojo.TbUser;
import com.usian.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/service/sso")
public class SSOController {

    @Autowired
    private SSOService ssoService;

    /*
    *  用户注册验证手机号，和用户名不重复,
    *  当checkFlag是1的话checkValue是用户名     当checkFlag是2的话checkValue是手机号
    * */
    @RequestMapping("/checkUserInfo/{checkValue}/{checkFlag}")
    public boolean checkUserInfo(@PathVariable String checkValue, @PathVariable Integer checkFlag){
        return ssoService.checkUserInfo(checkValue,checkFlag);
    }

    //用户注册
    @RequestMapping("/userRegister")
    public int userRegister(@RequestBody TbUser tbUser){
        return ssoService.userRegister(tbUser);
    }

    //用户登录
    @RequestMapping("/userLogin")
    public Map userLogin(@RequestParam String username, @RequestParam String password){
        return ssoService.userLogin(username,password);
    }

    //用户登录
    @RequestMapping("/getUserByToken/{token}")
    public TbUser getUserByToken(@PathVariable String token){
        return ssoService.getUserByToken(token);
    }

    //退出用户
    @RequestMapping("/logOut")
    public boolean logOut(String token){
        return ssoService.logOut(token);
    }
}
