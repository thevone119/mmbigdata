package com.bingo;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Spring的工具类
 * 用于多线程等环境下，通过Spring工具类获取Spring的内的service
 *
 * Created by huangtw on 2018-04-08.
 */
@Component
public class SpringUtil  implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
        System.out.println("========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext="+SpringUtil.applicationContext+"========");
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        if(applicationContext!=null){
            return applicationContext.getBean(clazz);
        }
        return null;
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }



    public static void main(String srgs[]){
        String str = "SysClientOPInfoAction,method:save1523947878617, ClientDownLoadAction,method:querySysClientVersion1523947943155, MobileViewAction,method:targetnoview1523947869241, UrlFilterAction,method:listAllUrlFilter1523947951345, LoginAction,method:loginToken1523948010875, SysClientOPInfoAction,method:save1523947876948, SysClientOPInfoAction,method:save1523947860833, NoticeAction,method:unlist1523947943576, SignBizAction,method:listReception1523947887244, MobileViewAction,method:targetnoview1523948010781, UserAction,method:doQuery1523947891003, SysClientOPInfoAction,method:save1523947884950, LoginAction,method:loginToken1523947947960, SignConfigClientAction,method:queryBizRule1523947951345, SysClientOPInfoAction,method:save1523947884763, NoticeAction,method:unlist1523947882314, LoginAction,method:loginToken1523948003402, NoticeAction,method:unlist1523948007349, NoticeAction,method:unlist1523947946150, SignRecReceptionCacheAction,method:save1523947862268, MobileViewAction,method:targetnoview1523947882688, MobileViewAction,method:targetnoview1523947886760, NoticeAction,method:unlist1523948011639, SysClientOPInfoAction,method:save1523947874779, LoginAction,method:loginToken1523948005852, LoginAction,method:loginToken1523947950472, SysClientOPInfoAction,method:save1523948011296, NoticeAction,method:unlist1523947884248, MobileViewAction,method:targetnoview1523947945277, SysClientOPInfoAction,method:save1523947859226, SignConfigClientAction,method:querySignProdConfig1523947881440, LoginAction,method:loginToken1523947953326, ClientDownLoadAction,method:querySysClientMsiVersion1523947881487, MobileViewAction,method:targetnoview1523948009814, NoticeAction,method:unlist1523947881284, SignRecReceptionCacheAction,method:save1523948012107, NoticeAction,method:unlist1523947889443, SysClientOPInfoAction,method:save1523947881409, UserAction,method:doQuery1523947890332, MobileViewAction,method:targetnoview1523948013371, SysClientOPInfoAction,method:save1523948003106, NoticeAction,method:unlist1523948010251, MobileViewAction,method:targetnoview1523947871971, LoginAction,method:loginToken1523948008410, ClientDownLoadAction,method:querySysClientVersion1523947889662, MobileViewAction,method:targetnoview1523947884264, MobileViewAction,method:targetnoview1523948003324, NoticeAction,method:unlist1523947943233, SignConfigClientAction,method:queryLingdao1523947881238, SignRecReceptionCacheAction,method:save1523947949614, MobileViewAction,method:targetnoview1523947942204, MobileViewAction,method:targetnoview1523947859756, NoticeAction,method:unlist1523948006850, SignRecReceptionCacheAction,method:save1523948002435, SysClientOPInfoAction,method:save1523947873984, NoticeAction,method:unlist1523947951096, SignConfigClientAction,method:queryBizRule1523947879912, UrlFilterAction,method:listAllUrlFilter1523947891066, NoticeAction,method:unlist1523948013995, SysClientOPInfoAction,method:save1523947851301, SignRecReceptionCacheAction,method:save1523947854468, SysClientOPInfoAction,method:save1523947882969, UserAction,method:doQuery1523947887805, MobileViewAction,method:targetnoview1523947876168, ClientDownLoadAction,method:querySysClientVersion1523948006912, LoginAction,method:loginToken1523947950799";
        String s[] = str.split(",");
        List<String[]> list  =new ArrayList<String[]>();
        for(int i=0;i<s.length-1;i++){
            String usetime = s[i+1].substring(s[i+1].length()-13);
            list.add(new String[]{s[i],usetime});
            i++;
        }
        Collections.sort(list, new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                int i = o1[1].compareTo( o2[1]) ;

                return i;
            }
        });
        for(String[] s1: list){
            System.out.println(s1[1]+":"+s1[0]);
        }
        //[SysClientOPInfoAction,method:save1523947878617, ClientDownLoadAction,method:querySysClientVersion1523947943155, MobileViewAction,method:targetnoview1523947869241, UrlFilterAction,method:listAllUrlFilter1523947951345, LoginAction,method:loginToken1523948010875, SysClientOPInfoAction,method:save1523947876948, SysClientOPInfoAction,method:save1523947860833, NoticeAction,method:unlist1523947943576, SignBizAction,method:listReception1523947887244, MobileViewAction,method:targetnoview1523948010781, UserAction,method:doQuery1523947891003, SysClientOPInfoAction,method:save1523947884950, LoginAction,method:loginToken1523947947960, SignConfigClientAction,method:queryBizRule1523947951345, SysClientOPInfoAction,method:save1523947884763, NoticeAction,method:unlist1523947882314, LoginAction,method:loginToken1523948003402, NoticeAction,method:unlist1523948007349, NoticeAction,method:unlist1523947946150, SignRecReceptionCacheAction,method:save1523947862268, MobileViewAction,method:targetnoview1523947882688, MobileViewAction,method:targetnoview1523947886760, NoticeAction,method:unlist1523948011639, SysClientOPInfoAction,method:save1523947874779, LoginAction,method:loginToken1523948005852, LoginAction,method:loginToken1523947950472, SysClientOPInfoAction,method:save1523948011296, NoticeAction,method:unlist1523947884248, MobileViewAction,method:targetnoview1523947945277, SysClientOPInfoAction,method:save1523947859226, SignConfigClientAction,method:querySignProdConfig1523947881440, LoginAction,method:loginToken1523947953326, ClientDownLoadAction,method:querySysClientMsiVersion1523947881487, MobileViewAction,method:targetnoview1523948009814, NoticeAction,method:unlist1523947881284, SignRecReceptionCacheAction,method:save1523948012107, NoticeAction,method:unlist1523947889443, SysClientOPInfoAction,method:save1523947881409, UserAction,method:doQuery1523947890332, MobileViewAction,method:targetnoview1523948013371, SysClientOPInfoAction,method:save1523948003106, NoticeAction,method:unlist1523948010251, MobileViewAction,method:targetnoview1523947871971, LoginAction,method:loginToken1523948008410, ClientDownLoadAction,method:querySysClientVersion1523947889662, MobileViewAction,method:targetnoview1523947884264, MobileViewAction,method:targetnoview1523948003324, NoticeAction,method:unlist1523947943233, SignConfigClientAction,method:queryLingdao1523947881238, SignRecReceptionCacheAction,method:save1523947949614, MobileViewAction,method:targetnoview1523947942204, MobileViewAction,method:targetnoview1523947859756, NoticeAction,method:unlist1523948006850, SignRecReceptionCacheAction,method:save1523948002435, SysClientOPInfoAction,method:save1523947873984, NoticeAction,method:unlist1523947951096, SignConfigClientAction,method:queryBizRule1523947879912, UrlFilterAction,method:listAllUrlFilter1523947891066, NoticeAction,method:unlist1523948013995, SysClientOPInfoAction,method:save1523947851301, SignRecReceptionCacheAction,method:save1523947854468, SysClientOPInfoAction,method:save1523947882969, UserAction,method:doQuery1523947887805, MobileViewAction,method:targetnoview1523947876168, ClientDownLoadAction,method:querySysClientVersion1523948006912, LoginAction,method:loginToken1523947950799]

    }
}

