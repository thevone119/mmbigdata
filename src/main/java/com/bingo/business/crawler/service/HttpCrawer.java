package com.bingo.business.crawler.service;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * http的网络爬虫
 * 1.包含多线程爬虫设计，异步请求响应模型
 * 2.包含普通HTTP链接设置（代理，cookie等）
 * 3.返回的http数据处理采用Jsoup进行数据处理
 * Created by huangtw on 2018-04-04.
 */
public class HttpCrawer {
        private int threadNum = 1;//处理线程数，多个线程进行处理，则修改此线程数




}
