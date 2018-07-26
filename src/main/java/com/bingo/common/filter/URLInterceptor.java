package com.bingo.common.filter;



import com.bingo.common.model.SessionUser;
import com.bingo.common.service.SessionCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 所有请求的拦截器
 * Created by Administrator on 2018-06-26.
 */
@Component
public class URLInterceptor  extends HandlerInterceptorAdapter {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final static String METHOD="method";
    private long currTime = 0;

    private static int currThread = 0;
    private static Map<String,String> currThreadMap = new HashMap<String,String>();
    private static final int  maxThread = 50;//最大允许并发线程数


    @Resource
    private SessionCacheService sessionCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        currTime=System.currentTimeMillis();
        if(isbusy()){//系统繁忙，终止部分请求
            response.sendRedirect("/error_busy.jsp");
            return false;
        }
        boolean ret = preHandleProxy(request,response,handler);
        HandlerMethod hm = (HandlerMethod)handler;
        String actionname = hm.getMethod().toString();
        if(ret){
            threadadd(actionname+currTime);
        }else{
            //直接跳转到首页
            response.sendRedirect("/");
        }
        return ret;
    }

    /**
     * 前置拦截处理
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    private boolean preHandleProxy(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        HandlerMethod hm = (HandlerMethod)handler;
        //1.获取方法的注解
        ControllerFilter filter = hm.getMethodAnnotation(ControllerFilter.class);
        //2.如果方法上没有注解，则获取类上的注解
        if(filter==null){
            filter = hm.getBean().getClass().getAnnotation(ControllerFilter.class);
        }
        //3.如果方法和类上都没有注解，则直接返回
        if(filter==null){
            return true;
        }
        //4.获取登录用户
        SessionUser user =(SessionUser)sessionCache.getLoginUser();

        //4.判断注解权限，判断是否登录
        if(filter.LoginType()==ControllerType.LOGIN_TYPE_LOGIN){
            if(user==null){
                logger.info("当前用户没有权限访问此页面01");
                return false;
            }
        }
        //4.判断注解权限，判断是否管理员
        if(filter.UserType()==ControllerType.USER_TYPE_ADMIN){
            if(user==null||user.getUsertype()!=1){
                logger.info("当前用户没有权限访问此页面02");
                return false;
            }
        }
        //4.判断注解权限，判断角色
        if(filter.roles()!=null && filter.roles().length>0){
            if(user==null){
                logger.info("当前用户没有权限访问此页面031");
                return false;
            }
            boolean hasrole = false;
            for(String role:filter.roles()){
                for(String urole:user.getRolecodes()){
                    if(role.equals(urole)){
                        hasrole = true;
                        break;
                    }
                }
            }
            if(!hasrole){
                logger.info("当前用户没有权限访问此页面032");
                return false;
            }
        }

        return true;
    }


    private static synchronized void threadadd(String key){
        currThread++;
        currThreadMap.put(key, null);
    }

    private static synchronized void threaddel(String key){
        currThread--;
        currThreadMap.remove(key);
    }

    private boolean isbusy(){
        if(currThread>=maxThread){
            logger.info("当前并发线程数(锁):"+currThread+currThreadMap.toString());
            return true;
        }
        logger.info("当前并发线程数:"+currThread);
        return false;
    }

    //返回处理
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //String param=request.getParameter(METHOD);
        HandlerMethod hm = (HandlerMethod)handler;

        String actionname = hm.getMethod().getDeclaringClass().getName()+"."+hm.getMethod().getName().toString();
        threaddel(actionname+currTime);
        //Runtime imp = Runtime.getRuntime();
        //String memory = ",maxMemory:"+imp.maxMemory()/1024/1024+"M,freeMemory:"+imp.freeMemory()/1024/1024+"M"+",userMemory:"+(imp.maxMemory()-imp.freeMemory())/1024/1024+"M";
        String memory = "";
        logger.info("Action end:"+actionname+",usetime:"+(System.currentTimeMillis()-currTime)+memory);
    }



    /**
     * 对字符串当中的JS代码 进行过滤,全部替换为"非法字符"
     *
     * @param str 要过滤的字符串
     * @return 过滤后的字符串
     */
    public static String doScriptFilter(String str) {
        str = Pattern.compile("<script.*?>.*?</script>", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("****");
        return str;
    }
}
