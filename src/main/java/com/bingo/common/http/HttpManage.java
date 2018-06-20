package com.bingo.common.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description   <操作HTTP和HTTPS的工具>
 * @about 
 * 使用连接池管理http请求，提高效率。比jdk自带的http要更好。不建议使用jdk的HttpURLConnection。
 * http必须使用的包是：
 * httpclient-4.5.2.jar，
 * httpcore-4.4.5.jar，
 * commons-codec-1.10.jar，
 * commons-logging-1.2.jar。
 * 
 * 文件上传需要使用的包是：
 * httpmime-4.5.2.jar。
 * 
 * 单元测试需要mockito-core-1.10.19.jar,objenesis-2.2.jar,slf4j-api-1.7.5.jar,slf4j-log4j12-1.7.5.jar包。
 * @author  李丽全
 * @since   2016年8月29日 23:40:27
 */
public class HttpManage {
	private static final int maxTotal = 200;
	private static final int maxPerRoute = 20;
	private static final int BUFFER = 8192;
	/**
	 * 使用场景：
	 * 调用移动的发短信网址，短信接口是50秒之后才有数据返回，
	 * 如果timeout超时设置太小，还没等到数据返回，代码就开始报错
	 * 了，所以，建议timeout的值设置大一些。
	 * 为什么要等到短信返回数据呢？因为我们需要获取返回数据，判断
	 * 是否发送短信成功，你懂的。如果发送短信失败，就提示用户，这样
	 * 我们的系统就显得更加友好。
	 */
	private static final int socketTimeout = 60000;  //建议设置成60000，测试的时候可以设置成10000
	private static final int connectionRequestTimeout = 60000;  //建议设置成60000
	private static final int connectTimeout = 60000;  //建议设置成60000
	
	//静态的连接池管理器，不需要每次都new一个，很耗费资源。
	private static PoolingHttpClientConnectionManager manager = null;
	
	/**
	 * @description <请求的http类型>
	 */
	public enum Type {POST, GET};
	
	private HttpHost httpHost;
	private CookieStore cookieStore;
	private static HttpClientContext localContext;  //使用cookie保持会话
	
	private static final String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 1.7; .NET CLR 1.1.4322; CIBA; .NET CLR 2.0.50727)";
	
	/**
	 * 以前写过一个HttpHandle.java的工具类，但是每次使用完之后，就关闭http连接了。
	 * 当时没有测试高并发的情况，所以没有发现什么问题。
	 * 但是，频繁的创建http连接是很耗费资源的，所以，这个新的工具类HttpManage.java
	 * 使用连接池的概念，重复利用http连接，节省系统资源和开销，加快请求的速度，从而为
	 * 我们提升效率。
	 * 使用该工具，可以制作网页爬虫，抓取任何想要的网页，然后分析网页内容，获取自己想要
	 * 的数据。甚至可以直接登录系统，下载或者上传数据，增删改查等等。只有你没想到的，没有
	 * 做不到的。自己去发掘应用场景吧。
	 * 
	 * 使用总结：
	 * 1、有些网址用post请求报错，可以尝试用get请求。这是别人系统的设置问题。（已验证）
	 * 2、如果post请求不能打开想要的页面，但是没有报错，那么使用get请求就可以了。（已验证）
	 * 3、CloseableHttpClient不需要关闭，不用担心http连接被用完的情况。（已验证）
	 * 4、高并发完全没问题。（已验证）
	 * 5、需要证书才能访问的网站，可以使用http请求，并且获得页面内容。（已验证）
	 * 6、自动保存cookie，保持会话。cookie很重要，特别是页面跳转的时候需要使用cookie，常用的是单点登录的校验。（已验证）
	 * 7、支持代理设置，特别是在移动内网需要访问百度的时候需要用到。（已验证）
	 * 8、支持下载文件，下载页面。（已验证）
	 * 9、支持微信请求。（已验证）
	 * 10、支持文件上传，模拟html的form表单提交。（已验证）
	 */
	
	/**
	 * @description   <初始化连接池>
	 */
	private void init() {
		try {
			if(manager == null) {
				SSLContext sslContext = SSLContexts.custom()
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
				HostnameVerifier verifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;  //忽略证书验证
				LayeredConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, verifier);
				Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register("https", factory)  //设置可以访问HTTPS
						.register("http", PlainConnectionSocketFactory.getSocketFactory())  //设置可以访问HTTP
						.build();
				manager = new PoolingHttpClientConnectionManager(registry);
				manager.setMaxTotal(maxTotal);
				manager.setDefaultMaxPerRoute(maxPerRoute);
				SocketConfig socketConfig = SocketConfig.custom()
						.setSoTimeout(socketTimeout).build();
				manager.setDefaultSocketConfig(socketConfig);
				
				cookieStore = new BasicCookieStore();
				localContext = HttpClientContext.create();
				localContext.setCookieStore(cookieStore);
			}
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpClient = null;
		try {
			init();
			Builder builder = RequestConfig.custom()
					.setConnectionRequestTimeout(connectionRequestTimeout)
					.setConnectTimeout(connectTimeout)
					.setSocketTimeout(socketTimeout);
			if(httpHost != null) {
				builder.setProxy(httpHost);  //设置代理
			}
			RequestConfig requestConfig = builder.build();
			httpClient = HttpClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(requestConfig)
					.build();
			if(manager != null && manager.getTotalStats() != null) {
				//这个输出不要注释掉，方便以后分析问题出现的原因。
				System.out.println(manager.getTotalStats().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpClient;
	}
	
	/**
	 * @description <创建代理>
	 * @about httpclient本来就是模拟IE浏览器的，如果有些url需要代理才能访问，那么httpclient就需要设置代理。
	 * @param hostname
	 * @param port
	 */
	public void setProxy(String hostname, int port) {
		try {
			httpHost = new HttpHost(hostname, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description   <清除cookie>
	 * @about   一般不建议清除cookie。如果清除cookie，会话就会断开了。
	 * 如果非要清理不可，那就清理吧。
	 */
	public void clearCookie() {
		if(localContext != null) {
			localContext.getCookieStore().clear();
		}
	}
	
	/**
	 * @description <创建post请求>
	 * @param url  请求路径
	 * @param requestParams  请求参数
	 * @return
	 */
	private HttpPost createHttpPost(String url, Map<String, String> requestParams) {
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			//有的网站会先判别用户的请求是否是来自浏览器，如不是，则返回不正确的文本，所以用httpclient时在头部加入如下信息
			httpPost.setHeader("User-Agent", userAgent);
			if(requestParams != null && requestParams.size() > 0) {
				List<NameValuePair> postParams = new ArrayList<NameValuePair>();
				for(String key: requestParams.keySet()) {
					postParams.add(new BasicNameValuePair(key, requestParams.get(key)));
				}
				HttpEntity postBodyEntity = new UrlEncodedFormEntity(postParams, "utf-8");
				httpPost.setEntity(postBodyEntity);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpPost;
	}
	
	/**
	 * @description <创建get请求>
	 * @param url  请求路径
	 * @param requestParams  请求参数
	 * @return
	 */
	private HttpGet createHttpGet(String url, Map<String, String> requestParams) {
		HttpGet httpGet = null;
		try {
			StringBuffer getParams = new StringBuffer();
			if(requestParams != null && requestParams.size() > 0) {
				int num = 0;
				if(!url.contains("?")) {
					getParams.append("?");
				} else {
					getParams.append("&");
				}
				
				for(String key: requestParams.keySet()) {
					getParams.append(URLEncoder.encode(key, "utf-8")).append("=").append(URLEncoder.encode(requestParams.get(key), "utf-8"));
					if(num != requestParams.size() - 1) {
						getParams.append("&");
					}
					num ++;
				}
			}
			httpGet = new HttpGet(url + getParams.toString());
			//有的网站会先判别用户的请求是否是来自浏览器，如不是，则返回不正确的文本，所以用httpclient时在头部加入如下信息
			httpGet.setHeader("User-Agent", userAgent);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpGet;
	}
	
	/**
	 * @description <执行post请求>
	 * @param url  请求路径
	 * @param requestParams  请求参数
	 * @return
	 */
	public String post(String url, Map<String, String> requestParams) {
		String responseText = null;
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient().execute(createHttpPost(url, requestParams), localContext);
			
			if(response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				//重定向
				if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY
						|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header[] headers = response.getHeaders("Location");
					if(headers != null && headers.length > 0) {
						//System.out.println("headers.length="+headers.length);
						for(Header header: headers) {
							//System.out.println("header.getValue()="+header.getValue());
							//重定向连接
							String redirectUrl = header.getValue();
							//不过好像只是能定向两次，超过三次就不行了。可能是response没有及时释放导致的。因此，释放response即可。
							response.close();  //必须关闭，否则连接会被用光的。
							return post(redirectUrl, null);
						}
					}
				} else {
					if(response.getEntity() != null) {
						//获取请求返回的内容，可能是完整的jsp页面，可能是json等字符串，可能没有返回内容。
						responseText = EntityUtils.toString(response.getEntity(), "utf-8");
					}
				}
			}
			
			return responseText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @description <执行get请求>
	 * @param url  请求路径
	 * @param requestParams  请求参数
	 * @return
	 */
	public String get(String url, Map<String, String> requestParams) {
		String responseText = null;
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient().execute(createHttpGet(url, requestParams), localContext);
			
			if(response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				//重定向
				if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY
						|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header[] headers = response.getHeaders("Location");
					if(headers != null && headers.length > 0) {
						//System.out.println("headers.length="+headers.length);
						for(Header header: headers) {
							//System.out.println("header.getValue()="+header.getValue());
							//重定向连接
							String redirectUrl = header.getValue();
							//不过好像只是能定向两次，超过三次就不行了。可能是response没有及时释放导致的。因此，释放response即可。
							response.close();  //必须关闭，否则连接会被用光的。
							return post(redirectUrl, null);
						}
					}
				} else {
					if(response.getEntity() != null) {
						//获取请求返回的内容，可能是完整的jsp页面，可能是json等字符串，可能没有返回内容。
						responseText = EntityUtils.toString(response.getEntity(), "utf-8");
					}
				}
			}
			
			return responseText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @description <下载文件>
	 * @about 可以下载任意文件，包括js文件，css文件，jsp文件等等。
	 * @param type
	 * @param url
	 * @param requestParams
	 * @param dir 文件存放的目录 
	 * @param fileName 文件名
	 * @param downloadPage 是否下载网页。true/下载，false/则不下载。
	 * @return
	 */
	public boolean download(Type type, String url, Map<String, String> requestParams, 
			String dir, String fileName, boolean downloadPage) {
		CloseableHttpResponse response = null;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			if(type == Type.POST) {
				response = getHttpClient().execute(createHttpPost(url, requestParams), localContext);
			} else if(type == Type.GET) {
				response = getHttpClient().execute(createHttpGet(url, requestParams), localContext);
			}
			/*
			System.out.println("-------charset--------="+getContentType(response));
			System.out.println("-------isPage--------="+isPage(response));
			*/
			if(!downloadPage && isPage(response)) {
				return false;
			}
			
			//文件已经存在，不重复下载。
			File file = new File(dir, fileName);
			if(file.exists() && file.isFile() 
					&& file.length() == response.getEntity().getContentLength()) {
				return true;
			}

			//文件是空的，不下载。
			//System.out.println(response.getEntity().getContentLength());
			//如果getContentLength()==-1，证明文件不为空。
			if(response.getEntity().getContentLength() == 0) {
				return false;
			}
			
			in = response.getEntity().getContent();
			out = new FileOutputStream(new File(dir + fileName));
			byte[] buffer = new byte[BUFFER];
			int length = 0;
			while((length = in.read(buffer)) != -1) {
				//System.out.println("---------------write file---------------");
				out.write(buffer, 0, length);
			}
			out.flush();
			
			return true;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * @description <获得返回结果的内容类型>
	 * @param response
	 * @return
	 */
	private String getContentType(HttpResponse response) {
		try {
			if(response != null
					&& response.getEntity() != null
					&& response.getEntity().getContentType() != null) {
				String contentType = response.getEntity().getContentType().getValue();
				if(StringUtils.isNotBlank(contentType)) {
					return contentType;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @description <是否jsp或者html页面>
	 * @param response
	 * @return
	 */
	private boolean isPage(HttpResponse response) {
		String contentType = getContentType(response);
		if(StringUtils.isNotBlank(contentType)
				&& contentType.indexOf("text/html") != -1) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @description   <微信POST请求>
	 * @about   专门用于微信的。
	 * @param url  url地址
	 * @param postDataXML  xml格式的字符串
	 * @return
	 */
	public String postWeiXin(String url, String postDataXML) {
		String responseText = null;
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			//有的网站会先判别用户的请求是否是来自浏览器，如不是，则返回不正确的文本，所以用httpclient时在头部加入如下信息
			httpPost.setHeader("User-Agent", userAgent);
			//参考微信的官网demo的HttpsRequest.java文件进行编写post参数
			StringEntity postEntity = new StringEntity(postDataXML, "utf-8");
			httpPost.setEntity(postEntity);
			response = getHttpClient().execute(httpPost);  //微信不需要保持cookie会话
			
			if(response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				//重定向
				if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY
						|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header[] headers = response.getHeaders("Location");
					if(headers != null && headers.length > 0) {
						//System.out.println("headers.length="+headers.length);
						for(Header header: headers) {
							//System.out.println("header.getValue()="+header.getValue());
							//重定向连接
							String redirectUrl = header.getValue();
							//不过好像只是能定向两次，超过三次就不行了。可能是response没有及时释放导致的。因此，释放response即可。
							response.close();  //必须关闭，否则连接会被用光的。
							return post(redirectUrl, null);
						}
					}
				} else {
					if(response.getEntity() != null) {
						//获取请求返回的内容，可能是完整的jsp页面，可能是json等字符串，可能没有返回内容。
						responseText = EntityUtils.toString(response.getEntity(), "utf-8");
					}
				}
			}
			
			return responseText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @description   <发送短信和彩信>
	 * @about   WebService也可以使用http来请求的。
	 * @param url   必须是?wsdl结尾。例如：http://hwt.zjportal.net/HWT/SMInterface.asmx?wsdl
	 * @param SOAPActionURI   例如：http://tempuri.org/Login
	 * @param postDataXML  xml格式的字符串
	 * @return
	 */
	public String postSMS(String url, String SOAPActionURI, String postDataXML) {
		String responseText = null;
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			//有的网站会先判别用户的请求是否是来自浏览器，如不是，则返回不正确的文本，所以用httpclient时在头部加入如下信息
			httpPost.setHeader("User-Agent", userAgent);
			/**
			 * 分析
			 * http://hwt.zjportal.net/HWT/SMInterface.asmx?op=Login
			 * 的报文格式，必须设置Content-Type，否则一直返回Internal Server Error
			 */
			httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
			httpPost.setHeader("SOAPAction", SOAPActionURI);  //设置访问WebService的某个action。
			//参考微信的官网demo的HttpsRequest.java文件进行编写post参数
			StringEntity postEntity = new StringEntity(postDataXML, "utf-8");
			httpPost.setEntity(postEntity);
			response = getHttpClient().execute(httpPost, localContext);  //根据文档说明，必须保持cookie会话
			
			if(response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				//重定向
				if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY
						|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					Header[] headers = response.getHeaders("Location");
					if(headers != null && headers.length > 0) {
						//System.out.println("headers.length="+headers.length);
						for(Header header: headers) {
							//System.out.println("header.getValue()="+header.getValue());
							//重定向连接
							String redirectUrl = header.getValue();
							//不过好像只是能定向两次，超过三次就不行了。可能是response没有及时释放导致的。因此，释放response即可。
							response.close();  //必须关闭，否则连接会被用光的。
							return post(redirectUrl, null);
						}
					}
				} else {
					if(response.getEntity() != null) {
						//获取请求返回的内容，可能是完整的jsp页面，可能是json等字符串，可能没有返回内容。
						responseText = EntityUtils.toString(response.getEntity(), "utf-8");
					}
				}
			}
			
			return responseText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @description   <文件上传>
	 * @about   POST请求发送文件和参数。
	 * 可以理解成html的form表单提交，表单中包含文件和参数。
	 * 
	 * 判断文件上传是否成功的方法：分析返回的内容即可。具体返回什么内容，就要看
	 * 服务器的业务逻辑了。
	 * @param url
	 * @param requestParams
	 * @param uploadFiles   上传的文件
	 * @return
	 */
	public String upload(String url, Map<String, String> requestParams, 
			List<File> uploadFiles) {
		String responseText = null;
		CloseableHttpResponse response = null;
		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			
			//以下两句，解决文件名的中文乱码问题。
			builder.setCharset(Charset.forName("utf-8"));  //设置请求的编码格式
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  //设置浏览器兼容模式
			
			HttpPost httpPost = new HttpPost(url);
			//有的网站会先判别用户的请求是否是来自浏览器，如不是，则返回不正确的文本，所以用httpclient时在头部加入如下信息
			httpPost.setHeader("User-Agent", userAgent);
			if(requestParams != null && requestParams.size() > 0) {
				for(String key: requestParams.keySet()) {
					//解决中文乱码（已验证）
					builder.addPart(key, new StringBody(requestParams.get(key), ContentType.create("text/plain", "utf-8")));
				}
			}
			if(uploadFiles != null && !uploadFiles.isEmpty()) {
				int num = 1;
				for(File uploadFile: uploadFiles) {
					/**
					 * 如果addPart的第一个参数相同，文件会覆盖。因此，第一个参数必须不同。
					 * builder.addPart("uploadFile" + num, new FileBody(uploadFile));
					 * 相当于html的代码
					 * <input name="uploadFile" type="file" />
					 */
					//解决文件名的中文乱码（已验证）
					builder.addPart("uploadFile" + num, new FileBody(uploadFile));
					num ++;
				}
			}
			HttpEntity multipartEntity = builder.build();
			httpPost.setEntity(multipartEntity);
			
			response = getHttpClient().execute(httpPost, localContext);
			if(response != null) {
				if(response.getEntity() != null) {
					//获取请求返回的内容，可能是完整的jsp页面，可能是json等字符串，可能没有返回内容。
					responseText = EntityUtils.toString(response.getEntity(), "utf-8");
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseText;
	}
}