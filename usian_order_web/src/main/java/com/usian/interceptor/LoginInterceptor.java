package com.usian.interceptor;

import com.usian.feign.SSOFeign;
import com.usian.pojo.TbUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private SSOFeign ssoFeign;
    //方法执行前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //通过Request取出请求参数
        String token = request.getParameter("token");
        //判断token是否为空
        if(StringUtils.isBlank(token)){
            return false;
        }
        TbUser tbUser = ssoFeign.getUserByToken(token);
        if(tbUser==null){
            return false;
        }
        return true;
    }
    //方法执行后，页面返回前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        
    }
    //方法执行后，页面返回后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
