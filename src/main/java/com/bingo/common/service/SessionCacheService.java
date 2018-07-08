package com.bingo.common.service;

import com.bingo.common.model.SessionUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 实现session的缓存处理类，所有的session相关内容均采用此类方法实现，不要使用常规的session
 *
 * Created by Administrator on 2018-06-25.
 */
@Service
public class SessionCacheService {

    @Resource
    private RedisCacheService redis;

    /**
     * 获取登录用户
     * @return
     */
    public SessionUser getLoginUser(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object luser= request.getSession().getAttribute("_LOGIN_USER");
        if(luser==null){
            String sessionid = getCurrSessionId();
            luser =  redis.get("_LOGIN_USER"+sessionid);
        }
        return (SessionUser)luser;
    }

    /**
     * 获取当前请求的sessionid
     *
     * @return
     */
    public String getCurrSessionId(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String sessionid = request.getParameter("token");
        if(sessionid==null||sessionid.length()<10){
            sessionid = request.getSession().getId();
        }
        return sessionid;
    }

    /**
     * 登录成功，则把登录用户放入session
     * @param obj
     */
    public void setloginUser(SessionUser obj){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String sessionid = request.getParameter("token");
        if(sessionid!=null && sessionid.length()>10){
            //保存24小时
            redis.set("_LOGIN_USER"+sessionid,obj,60*24);
        }else{
            request.getSession().setAttribute("_LOGIN_USER",obj);
        }
    }

    /**
     * 退出登录
     */
    public void loginOut(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String sessionid = request.getParameter("token");
        if(sessionid!=null && sessionid.length()>10){
            //保存24小时
            redis.delete("_LOGIN_USER"+sessionid);
        }else{
            request.getSession().removeAttribute("_LOGIN_USER");
        }
    }


}
