package com.bingo.test;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2018-08-04.
 */
public class TestBaiduOcr {
    //设置APPID/AK/SK
    public static final String APP_ID = "11633814";
    public static final String API_KEY = "AqjSqG2u3wiknY5hvVqTOXbS";
    public static final String SECRET_KEY = "m7yySvlOUQ5sIII1GcBkL1uFIvtsbCFZ";

    public static void main(String[] args) throws JSONException {
        long currTime = System.currentTimeMillis();
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        System.out.println(System.currentTimeMillis()-currTime);
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        // 调用接口
        String path = "d:/9.jpg";

        JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));
        System.out.println(System.currentTimeMillis()-currTime);

        //第2次识别
        currTime = System.currentTimeMillis();
        res = client.basicGeneral("d:/9.96.jpg", new HashMap<String, String>());
        JSONArray arr = res.getJSONArray("words_result");
        for(int i=0;i<arr.length();i++){
            String words = arr.getJSONObject(i).getString("words");
            if(words!=null && words.indexOf("￥")!=-1){
                words = words.replace("￥","");
                if(words.indexOf(".")==-1){
                    words = words.substring(0,words.length()-2)+"."+words.substring(words.length()-2);
                }
                System.out.println(words);
            }
        }

        System.out.println(res.toString());
        System.out.println(System.currentTimeMillis()-currTime);

    }
}
