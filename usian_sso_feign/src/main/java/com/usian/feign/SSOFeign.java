package com.usian.feign;


import com.usian.pojo.TbUser;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("usian-sso-service")
public interface SSOFeign {

    /*
    *  用户注册验证手机号，和用户名不重复,
    *  当checkFlag是1的话checkValue是用户名     当checkFlag是2的话checkValue是手机号
    * */
    @RequestMapping("/service/sso/checkUserInfo/{checkValue}/{checkFlag}")
    boolean checkUserInfo(@PathVariable String checkValue, @PathVariable Integer checkFlag);

    //用户注册
    @RequestMapping("/service/sso/userRegister")
    int userRegister(TbUser tbUser);

    //用户登录
    @RequestMapping("/service/sso/userLogin")
    Map userLogin(@RequestParam String username,@RequestParam String password);

    //通过token查询用户信息,查询用户登录是否过期
    @RequestMapping("/service/sso/getUserByToken/{token}")
    TbUser getUserByToken(@PathVariable String token);

    //退出用户
    @RequestMapping("/service/sso/logOut")
    boolean logOut(@RequestParam String token);
}
