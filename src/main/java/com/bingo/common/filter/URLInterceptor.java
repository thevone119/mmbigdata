package com.bingo.common.filter;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        currTime=System.currentTimeMillis();
        if(isbusy()){//系统繁忙，终止部分请求
            response.sendRedirect("/error_busy.jsp");
            return false;
        }
        boolean ret = preHandleProxy(request,response,handler);
        String param=request.getParameter(METHOD);
        String actionname = handler.getClass().getName()+",method:"+param;
        if(ret){
            threadadd(actionname+currTime);
        }
        return ret;
    }

    private boolean preHandleProxy(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

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

        String actionname = hm.getMethod().toString();
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
