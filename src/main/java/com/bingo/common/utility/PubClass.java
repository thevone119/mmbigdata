package com.bingo.common.utility;


import com.bingo.common.exception.DaoException;
import com.bingo.common.service.RedisCacheService;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/26.
 */
@Service
public class PubClass {

    @Resource
    private RedisCacheService redisCacheService;

    //region 分页逻辑
    public Pageable createPagenation() {
        return createPagenation("id");
    }

    public Pageable createPagenation(String sortKey) {
        int page = WebClass.requestToInt("pageNo");
        int pageSize = WebClass.requestToInt("pageSize");

        if (page > 0 && pageSize > 0) {
            Sort sort = new Sort(Sort.Direction.DESC, sortKey);
            return new PageRequest(page - 1, pageSize, sort);
        } else {
            return null;
        }
    }


    //region 权限验证




    //region 发送短信
    // 登录到短信验证服务器
    private XJsonInfo loginSMSServer(HttpClientContext httpClient) {
        String URL = "http://hwt.zjportal.net/HWT/SMInterface.asmx?wsdl";
        String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><Login xmlns=\"http://tempuri.org/\"><strUserName>ow0cs2kl</strUserName><strPwd>ecue82sw</strPwd><VerInfo>1.0</VerInfo></Login></soap:Body></soap:Envelope>";

        HttpHost proxy = WebClass.getProxy();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml; charset=UTF-8");
        headers.put("SOAPAction", "http://tempuri.org/Login");
        XJsonInfo re = WebClass.httpPostRequest(URL, headers, body, proxy, httpClient);
        if (!re.getSuccess()) {
            return re;
        }
        String response = re.toDataString();
        String nodeVal = XmlClass.toXML(response, true).selectSingleNode("//LoginResult").getText();
        if (StringUtils.isNotBlank(nodeVal) && Integer.parseInt(nodeVal) > 0) {
            return re;
        } else {
            return XJsonInfo.error().setMsg("短信服务器登录验证失败");
        }
    }

    /**
     * @description <发送短信>
     * @about 已验证
     */
    public XJsonInfo sendSMS(String mobile, String msg) {
        String URL = "http://hwt.zjportal.net/HWT/SMInterface.asmx?wsdl";
        HttpClientContext httpClient = new HttpClientContext();
        XJsonInfo re = loginSMSServer(httpClient);
        if (!re.getSuccess()) {
            return re;
        }

        StringBuffer sms = new StringBuffer();
        sms.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sms.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sms.append("<soap:Body>");
        sms.append("<SendSms xmlns=\"http://tempuri.org/\">");
        sms.append("<strMobile>").append(mobile).append("</strMobile>");
        sms.append("<strSmsPort>").append("10657055518000113").append("</strSmsPort>");
        sms.append("<strSmsMsg>").append(msg).append("</strSmsMsg>");
        sms.append("</SendSms>");
        sms.append("</soap:Body>");
        sms.append("</soap:Envelope>");

        HttpHost proxy = WebClass.getProxy();
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml; charset=UTF-8");
        headers.put("SOAPAction", "http://tempuri.org/SendSms");
        re = WebClass.httpPostRequest(URL, headers, sms.toString(), proxy, httpClient);
        if (!re.getSuccess()) {
            return re;
        }

        String response = re.toDataString();
        //System.out.println(response);
        Document doc = XmlClass.toXML(response, true);
        String nodeVal = doc.selectSingleNode("//SendSmsResult").getText();
        //System.out.println(nodeVal);
        if (StringUtils.isNotBlank(nodeVal) && Integer.parseInt(nodeVal) > 0) {
            String okMsg = doc.selectSingleNode("//SendSmsResult").getText();
            if (StringUtils.isNotBlank(okMsg)) {
                return XJsonInfo.success().setMsg(okMsg);
            }
            return XJsonInfo.success();
        } else {
            String errMsg = doc.selectSingleNode("//ErrorMsg").getText();
            if (StringUtils.isNotBlank(errMsg)) {
                return XJsonInfo.error().setMsg(errMsg);
            } else {
                return XJsonInfo.error().setMsg("短信发送失败");
            }
        }
    }

//    public XJsonInfo sendSMS(List<Long> userIds, String msg) {
//        List<String> list = userService.getUserMobile(userIds);
//        XJsonInfo re = new XJsonInfo();
//        for (String num : list) {
//            re = sendSMS(num, msg);
//            if (!re.getSuccess()) {
//                break;
//            }
//        }
//        return re;
//    }
    //endregion


    public void showLog(String content) {
        Logger logger = LogManager.getLogger(PubClass.class.getName());
        logger.info(content);
    }

    public void showLog(String content, String name) {
        Logger logger = LogManager.getLogger(name);
        logger.info(content);
    }

    public void showLog(String content, Class<?> clazz) {
        Logger logger = LogManager.getLogger(clazz);
        logger.info(content);
    }

    public void showErrorLog(Object content, Class<?> clazz) {
        Logger logger = LogManager.getLogger(clazz);
        logger.error(content);
    }
    //endregion

    //region 缓存
    public void setCache(String key, Object obj) {
        redisCacheService.set(key, obj);
    }

    public void setCache(String key, Object obj, int mins) {
        redisCacheService.set(key, obj, mins);
    }

    public Object getCache(String key) {
        return redisCacheService.get(key);
    }

    public void removeCache(String key) {
        redisCacheService.delete(key);
    }
    //endregion
}
