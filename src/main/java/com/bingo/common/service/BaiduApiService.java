package com.bingo.common.service;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-08-05.
 * 百度开放平台接口
 */
@Service
public class BaiduApiService {

    @Autowired
    private RedisCacheService redis;

    @Value("${baidu.APP_ID}")
    private String app_id;

    @Value("${baidu.API_KEY}")
    private String api_key;

    @Value("${baidu.SECRET_KEY}")
    private String secret_key;

    private static AipOcr aipocr = null;


    private synchronized AipOcr  getAipOcr(){
        //初始化一个AipOcr,这个缓存起来，避免重复认证
        String checktimeout = (String)redis.get("baidu.apiocr");
        if(aipocr==null||checktimeout==null){
            aipocr = new AipOcr(app_id, api_key, secret_key);
            // 可选：设置网络连接参数
            aipocr.setConnectionTimeoutInMillis(2000);
            aipocr.setSocketTimeoutInMillis(60000);
            //缓存24小时吧，其实这个1个月才失效
            redis.set("baidu.apiocr","checktimeout",60*24);
        }
        return aipocr;
    }

    /**
     * 通用的图片识别
     * @param image
     * @param options
     * @return
     */
    public JSONObject basicGeneral(byte[] image, HashMap<String, String> options){
        AipOcr aipocr = getAipOcr();
        JSONObject res = aipocr.basicGeneral(image, options);
        return res;
    }

    /**
     * 二维码识别
     * @return
     */
    public JSONObject qrcode(byte[] image, HashMap<String, String> options){
        AipOcr aipocr = getAipOcr();
        JSONObject res = aipocr.qrcode(image, options);
        return res;
    }

}
