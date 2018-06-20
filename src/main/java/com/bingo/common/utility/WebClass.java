package com.bingo.common.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/26.
 */
public class WebClass {
    public static boolean isProxy = false;
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        return response;
    }

    // 考虑到可能用了反向代理，这里如果读取到本地ip，需要考虑从反向代理约定好header中读取代理前的ip
    public static String getIP() {
        HttpServletRequest request = getRequest();
        String ip = request.getRemoteAddr();
        if (ip.contains("0:0:0:0") || ip.contains("127.0.0.1")) {
            String nIP = "";
            nIP = request.getHeader("X-Real-IP"); //REVIEW 如果经过多级代理，还需要再处理
            if (StringUtils.isNotBlank(nIP)) {
                ip = nIP;
            }
        }
        return ip;
    }

    // 考虑到可能用了反向代理，这里如果读取到host，需要考虑从反向代理约定好header中读取代理前的host
    public static String getHost() {
        HttpServletRequest request = getRequest();
        String host = request.getRemoteHost();
        if (host.contains("localhost") || host.contains("127.0.0.1")) {
            String nHost = "";
            nHost = request.getHeader("X-Host"); //REVIEW 如果经过多级代理，还需要再处理
            if (StringUtils.isNotBlank(nHost)) {
                host = nHost;
            }
        }
        return host;
    }

    // 获得基础请求地址
    public static String getBaseUri() {
        HttpServletRequest request = getRequest();
        String path = request.getContextPath();
        String baseUri = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + path + "/";

        return baseUri;
    }

    // 页面跳转
    public static void sendRedirect(String url) {
        HttpServletResponse response = getResponse();
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String requestToString(String key) {
        HttpServletRequest request = getRequest();
        String va;
        va = request.getParameter(key);
        if (StringClass.isNullOrEmpty(va)) {
        }
        return va;
    }

    public static int requestToInt(String key) {
        HttpServletRequest request = getRequest();
        String va;
        va = request.getParameter(key);
        if (StringClass.isNullOrEmpty(va)) {

        }
        try {
            return Integer.parseInt(va);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static long requestToLong(String key) {
        HttpServletRequest request = getRequest();
        String va;
        va = request.getParameter(key);
        if (StringClass.isNullOrEmpty(va)) {

        }
        try {
            return Long.valueOf(va);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static PoolingHttpClientConnectionManager cm;

    private static void initHttpClient() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(300);// 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
        }
    }

    /**
     * 通过连接池获取HttpClient
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient(CookieStore cookieStore) {
        initHttpClient();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000) // 创建连接的最长时间
                .setConnectionRequestTimeout(1000)  // 从连接池中获取到连接的最长时间
                .setSocketTimeout(4000)  // 数据传输的最长时间
                .setExpectContinueEnabled(true)  // 提交请求前测试连接是否可用
                .build();

        return HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(cm) // 目前暂时不使用连接池技术
                //.setConnectionManagerShared(true) // 如果启用连接池技术，请开启这个
                .setDefaultRequestConfig(requestConfig)
                //.setRetryHandler(retryHandler)

                .build();
    }

    /**
     * 处理Http请求
     *
     * @param request
     * @return
     */
    public static XJsonInfo getResult(HttpRequestBase request, CookieStore cookieStore, HttpClientContext context) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient(cookieStore);
        try {
            response = httpClient.execute(request, context);
            // response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // long len = entity.getContentLength();// -1 表示长度未知
                String result = EntityUtils.toString(entity, "utf-8");
                // response.close();
                // httpClient.close();

                //REVIEW 这部分特别特殊，获得sessionid
                String sessionid = "";

                Header hed = response.getFirstHeader("Set-Cookie");
                if (hed != null) {
                    String cookiesStr = hed.getValue();
                    try {
                        sessionid = cookiesStr.split(";")[0].split("=")[1];
                    } catch (Exception ex) {

                    }
                }

                if (StringUtils.isBlank(sessionid) && context != null) {
                    List<Cookie> listCo = context.getCookieStore().getCookies();
                    if (listCo.size() > 0) {
                        for (Cookie co : listCo) {
                            if (co.getName().equalsIgnoreCase("JSESSIONID")) {
                                sessionid = co.getValue();
                            }
                        }
                    }
                }
                return XJsonInfo.success().setMsg(sessionid).setData(result);
            }
            return XJsonInfo.error();
        } catch (ClientProtocolException e) {
            return XJsonInfo.error().setMsg(e.getMessage());
        } catch (IOException e) {
            return XJsonInfo.error().setMsg(e.getMessage());
        } catch (Exception e) {
            return XJsonInfo.error().setMsg(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                //采用连接池的话，这个一定不能关闭。
                //httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static XJsonInfo getResult(HttpRequestBase request) {
        return getResult(request, null, null);
    }

    private static XJsonInfo getResult(HttpRequestBase request, CookieStore cookieStore) {
        // CloseableHttpClient httpClient = HttpClients.createDefault();

        return getResult(request, cookieStore, null);
    }

    public static HttpHost getProxy() {
        return new HttpHost("cmproxy.gmcc.net", 8081, "http");
    }

    // 检测是否需要启用代理
    private static boolean chkNeedProxy(String url) {
        if(!isProxy){
            return isProxy;
        }
        if (url.contains("http://10.")) {
            return false;
        }

        if (url.contains("gmcc.net")) {
            return false;
        }

        return true;
    }

    public static CloseableHttpClient craeteHttpClient() {
        return getHttpClient(null);
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }

        return pairs;
    }

    public static XJsonInfo httpGetRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpGet.setConfig(config);
        }
        return getResult(httpGet);
    }

    public static XJsonInfo httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpGet.setConfig(config);
        }
        return getResult(httpGet);
    }

    public static XJsonInfo httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws URISyntaxException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpGet.setConfig(config);
        }

        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static XJsonInfo httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpPost.setConfig(config);
        }
        return getResult(httpPost);
    }

    public static XJsonInfo httpPostRequest(String url, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpPost.setConfig(config);
        }
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getResult(httpPost);
    }

    public static XJsonInfo httpPostRequest(String url, Map<String, Object> params, boolean useProxy) {
        HttpPost httpPost = new HttpPost(url);
        if (useProxy) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpPost.setConfig(config);
        }
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getResult(httpPost);
    }

    public static XJsonInfo httpPostRequest(String url, Map<String, Object> params, HttpHost proxyHost) {
        HttpPost httpPost = new HttpPost(url);
        if (proxyHost != null) {
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpPost.setConfig(config);
        }

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getResult(httpPost);
    }

    public static XJsonInfo httpPostRequest(String url, String body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(body, "utf-8"));
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpPost.setConfig(config);
        }
        return getResult(httpPost);
    }

    public static XJsonInfo httpPostRequest(String url, Map<String, Object> headers, CookieStore cookieStore, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        if (chkNeedProxy(url)) {
            HttpHost proxyHost = getProxy();
            RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
            httpPost.setConfig(config);
        }
        if (headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            return XJsonInfo.error().setMsg(e.getMessage());
        }

        return getResult(httpPost, cookieStore);
    }

    public static XJsonInfo httpPostRequest(String url, Map<String, Object> headers, String body, HttpHost proxyHost, HttpClientContext httpClient) {

        HttpPost httpPost = new HttpPost(url);
        RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
        httpPost.setConfig(config);

        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        httpPost.setEntity(new StringEntity(body, "utf-8"));

        return getResult(httpPost, null, httpClient);
    }

}
