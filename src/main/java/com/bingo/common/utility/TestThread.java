package com.bingo.common.utility;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 多线程压力测试
 * @author huangtw
 *
 */
public class TestThread  extends Thread{


	public TestThread(){

	}

	public static void main(String args[]) throws InterruptedException{
		for(int i=0;i<20;i++){
			Thread.sleep(100);
			new TestThread().start();
		}
	}

	
	@Override  
	public void run() {
		while(true){
			long currTime = System.currentTimeMillis();
			Map<String, Object> params = new HashMap<>();
			XJsonInfo re = httpPostRequest("http://10.252.36.167:8082/taskProcessing/login.action?method=mpage", params);
			String date = "";
			if(re.getSuccess()){
				date = re.getData().toString().substring(0,100);
			}
			currstep++;
			System.out.println(currstep+"请求耗时:"+(System.currentTimeMillis()-currTime)+"_"+date);
		}
	}

	private static PoolingHttpClientConnectionManager cm;
	private static long currstep =0;
	private static synchronized void initHttpClient() {
		if (cm == null) {
			cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(100);// 整个连接池最大连接数
			cm.setDefaultMaxPerRoute(50);//  每路由最大连接数，针对一个域名的最大连接数，默认值是2
		}
	}

	/**
	 * 请求HTTP，单独写个方法处理，避免出现公共类改动，接口异常
	 * @param url
	 * @param params
	 * @return
	 */
	private  static XJsonInfo httpPostRequest(String url, Map<String, Object> params) {
		XJsonInfo ret =  new XJsonInfo();
		ret.setSuccess(true);
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = new HttpPost(url);
		try{
			if(params!=null){
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				//默认加入一个随机变量
				pairs.add(new BasicNameValuePair("__tid", System.currentTimeMillis()+""));
				for (Map.Entry<String, Object> param : params.entrySet()) {
					pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
			}
			initHttpClient();
			RequestConfig requestConfig = RequestConfig.custom()
					.setConnectTimeout(5000) // 创建连接的最长时间 5秒，默认-1
					.setConnectionRequestTimeout(2000)  // 从连接池中获取到连接的最长时间，2秒，超过2秒重新创建连接
					.setSocketTimeout(30000)  // 数据传输的最长时间,30秒，超过30秒算超时
					//.setExpectContinueEnabled(true)  // 提交请求前测试连接是否可用,这个参数不能设置，设置了并发高就超时了
					.build();
			httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
			//httpClient = HttpClients.createDefault();
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if(entity!=null){
				//java.io.InputStream in=response.getEntity().getContent();
				//String result = org.apache.commons.io.IOUtils.toString(in,"utf-8");
				//in.close();
				String result = EntityUtils.toString(entity, "utf-8");
				//关闭IO
				//entity.getContent().close();
				ret.setData(result);
			}
		}catch(Exception e){
			e.printStackTrace();
			ret.setSuccess(false);
			ret.setMsg(e.getMessage());
		}finally{
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				//httpClient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

}
