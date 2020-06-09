package com.usian.service;

import com.bjsxt.utils.MD5Utils;
import com.usian.mapper.TbUserMapper;
import com.usian.pojo.TbUser;
import com.usian.pojo.TbUserExample;
import com.usian.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SSOServiceImpI implements SSOService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Value("${USER_INFO}")
    private String USER_INFO;

    @Value("${SESSION_EXPIRE}")
    private Long SESSION_EXPIRE;

    @Autowired
    private RedisClient redisClient;
     /*
     *  用户注册验证手机号，和用户名不重复,
     *  当checkFlag是1的话checkValue是用户名     当checkFlag是2的话checkValue是手机号
     * */
    @Override
    public boolean checkUserInfo(String checkValue, Integer checkFlag) {
        //首先设置查询条件
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        //判读checkFlag
        if(checkFlag==1){   //用户名
            criteria.andUsernameEqualTo(checkValue);
        }else if(checkFlag==2){ //手机号
            criteria.andPhoneEqualTo(checkValue);
        }
        //进行查询
        List<TbUser> tbUserList = tbUserMapper.selectByExample(tbUserExample);
        //判读是否有数据
        if(tbUserList==null || tbUserList.size()==0){
            return true;
        }
        return false;
    }

    //用户注册
    @Override
    public int userRegister(TbUser tbUser) {
        //补充tbUser
        Date date = new Date();
        tbUser.setCreated(date);
        tbUser.setUpdated(date);
        //对密码进行加密
        tbUser.setPassword(MD5Utils.digest(tbUser.getPassword()));
        //添加用户
        return tbUserMapper.insertSelective(tbUser);
    }

    //用户登录
    @Override
    public Map userLogin(String username, String password) {
        //先将密码进行加密
        password = MD5Utils.digest(password);
        //查询登录的用户是否存在
        TbUserExample tbUserExample = new TbUserExample();
        TbUserExample.Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(password);
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if(tbUsers==null || tbUsers.size()==0){
            return null;
        }
        //设置token
        String token = UUID.randomUUID().toString();
        //存在后查出存到redis中，设置有效期
        TbUser tbUser = tbUsers.get(0);
        tbUser.setPassword(null);
        redisClient.set(USER_INFO+":"+token,tbUser);
        redisClient.expire(USER_INFO+":"+token,SESSION_EXPIRE);
        //定义map返回前台
        Map<String,String> map = new HashMap<>();
        map.put("userid",tbUser.getId().toString());
        map.put("username",tbUser.getUsername());
        map.put("token",token);
        return map;
    }


    //通过token查询用户，看是否失效
    @Override
    public TbUser getUserByToken(String token) {
        TbUser tbUser = (TbUser)redisClient.get(USER_INFO+":"+token);
        if(tbUser!=null){
            return tbUser;
        }
        return null;
    }


    //退出用户
    @Override
    public boolean logOut(String token) {
        Boolean del = redisClient.del(USER_INFO + ":" + token);
        return del;
    }
}
