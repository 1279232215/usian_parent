package com.usian.service;

import com.usian.pojo.TbUser;

import java.util.Map;

public interface SSOService {
    boolean checkUserInfo(String checkValue, Integer checkFlag);

    int userRegister(TbUser tbUser);

    Map userLogin(String username, String password);

    TbUser getUserByToken(String token);

    boolean logOut(String token);
}
