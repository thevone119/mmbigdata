package com.bingo.action;

import com.bingo.business.pay.parameter.PayInput;
import com.bingo.business.pay.parameter.PayReturn;
import com.bingo.common.model.ReturnObject;
import com.bingo.common.utility.PubClass;
import com.bingo.common.utility.QRCodeUtils;
import com.bingo.common.utility.XJsonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/19.
 * 定义一些通用的服务接口
 */
@RestController
@RequestMapping("/comm")
public class CommonController {
    @Autowired
    HttpServletRequest request;

    @Value("${heimi.pay.url}")
    private String heimipayurl;

    /**
     * 获取当前系统时间
     * @return
     * @throws Exception
     */
    @RequestMapping("/sys_time")
    public ReturnObject sys_time(String fromat) throws Exception {
        if(fromat==null||fromat.length()<3){
            //fromat = "yyyy-MM-dd HH:mm:ss";
            //默认返回getTime格式的时间
            return new ReturnObject(0,"ok",new Date().getTime());
        }
        SimpleDateFormat format2 = new SimpleDateFormat(fromat);
        return new ReturnObject(0,"ok",format2.format(new Date()));
    }

    /**
     * 测试服务是否可用
     * 1.返回提交过来的p参数
     * 2.返回可用的真实的url
     * @param p
     * @return
     * @throws Exception
     */
    @RequestMapping("/test_service")
    public Map<String,String> test_service(String p,String app) throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        map.put("p",p);
        map.put("url",heimipayurl);
        if(app!=null && app.equals("heimipay")){
            map.put("url",heimipayurl);
        }
        return map;
    }

    /**
     * 下载二维码
     * @param url
     * @return
     * @throws Exception
     */
    @RequestMapping("/down_qr_img")
    public void down_qr_img(HttpServletResponse response, String url) throws Exception {
        if(url==null||url.length()>255){
            return ;
        }
        //设置MIME类型
        response.setContentType("application/octet-stream");
        //或者为response.setContentType("application/x-msdownload");
        //设置头信息,设置文件下载时的默认文件名，同时解决中文名乱码问题
        response.addHeader("Content-disposition", "attachment;filename="+new String("二维码下载.png".getBytes(), "ISO-8859-1"));
        OutputStream out = response.getOutputStream();
        QRCodeUtils.createQRcode(url,out);
        out.flush();
        out.close();
    }













}
