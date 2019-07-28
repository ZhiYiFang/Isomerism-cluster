/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.impl.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class HttpUtils {

	// utf-8字符编码
	public static final String CHARSET_UTF_8 = "utf-8";

	// HTTP内容类型。
	public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";

	// HTTP内容类型。相当于form表单的形式，提交数据
	public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

	// HTTP内容类型。相当于form表单的形式，提交数据
	public static final String CONTENT_TYPE_JSON_URL = "application/json";

	// 连接管理器
	private static PoolingHttpClientConnectionManager pool;

	// 请求配置
	private static RequestConfig requestConfig;

	static {

		try {
			// System.out.println("初始化HttpClientTest~~~开始");
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
			// 配置同时支持 HTTP 和 HTPPS
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
			// 初始化连接管理器
			pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			// 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
			pool.setMaxTotal(200);
			// 设置最大路由
			pool.setDefaultMaxPerRoute(2);
			// 根据默认超时限制初始化requestConfig
			int socketTimeout = 10000;
			int connectTimeout = 10000;
			int connectionRequestTimeout = 10000;
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
					.setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();

		} catch (NoSuchAlgorithmException e) {
		} catch (KeyStoreException e) {
		} catch (KeyManagementException e) {
		}

		// 设置请求超时时间
		requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000)
				.setConnectionRequestTimeout(50000).build();
	}

	public static CloseableHttpClient getHttpClient() {

		CloseableHttpClient httpClient = HttpClients.custom()
				// 设置连接池管理
				.setConnectionManager(pool)
				// 设置请求配置
				.setDefaultRequestConfig(requestConfig)
				// 设置重试次数
				.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();

		return httpClient;
	}

	public static String getBasicURL(String ip, int port, String url) {
		return "http://" + ip + ":" + port + url;
	}

	private static String sendHttpPut(HttpPut httpPut) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = getHttpClient();
			// 配置请求信息
			httpPut.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpPut);
			// 得到响应实例
			HttpEntity entity = response.getEntity();

			// 可以获得响应头
			// Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
			// for (Header header : headers) {
			// System.out.println(header.getName());
			// }

			// 得到响应类型
			// System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

			// 判断响应状态
//			if (response.getStatusLine().getStatusCode() >= 300) {
//				throw new Exception(
//						"HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
//			}
			responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
			EntityUtils.consume(entity);


		} catch (Exception e) {
		} finally {
			try {
				// 释放资源
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
		return responseContent;
	}
	private static String sendHttpPost(HttpPost httpPost) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = getHttpClient();
			// 配置请求信息
			httpPost.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpPost);
			// 得到响应实例
			HttpEntity entity = response.getEntity();

			// 可以获得响应头
			// Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
			// for (Header header : headers) {
			// System.out.println(header.getName());
			// }

			// 得到响应类型
			// System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());



			responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
			EntityUtils.consume(entity);


		} catch (Exception e) {
		} finally {
			try {
				// 释放资源
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
		return responseContent;
	}

	private static String sendHttpGet(HttpGet httpGet) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = getHttpClient();
			// 配置请求信息
			httpGet.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpGet);
			// 得到响应实例
			HttpEntity entity = response.getEntity();

			// 可以获得响应头
			// Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
			// for (Header header : headers) {
			// System.out.println(header.getName());
			// }

			// 得到响应类型
			// System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

			// 判断响应状态
//			if (response.getStatusLine().getStatusCode() >= 300) {
//				throw new Exception(
//						"HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
//			}

			responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
			EntityUtils.consume(entity);
//			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
//					|| HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode()) {
//			}

		} catch (Exception e) {
		} finally {
			try {
				// 释放资源
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
		return responseContent;
	}

	private static String sendHttpDelete(HttpDelete httpDelete) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = getHttpClient();
			// 配置请求信息
			httpDelete.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpDelete);
			// 得到响应实例
			HttpEntity entity = response.getEntity();

			// 可以获得响应头
			// Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
			// for (Header header : headers) {
			// System.out.println(header.getName());
			// }

			// 得到响应类型
			// System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

			// 判断响应状态
//			if (response.getStatusLine().getStatusCode() >= 300) {
//				throw new Exception(
//						"HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
//			}

			responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
			EntityUtils.consume(entity);
//			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
//					|| HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode()
//					|| HttpStatus.SC_NO_CONTENT == response.getStatusLine().getStatusCode()) {
//			}

		} catch (Exception e) {
		} finally {
			try {
				// 释放资源
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
		return responseContent;
	}

	/**
	 * post请求
	 * 
	 * @param httpUrl
	 *            地址
	 * @return json结果
	 */
	public static String sendHttpPost(String httpUrl) {
		// 创建httpPost
		HttpPost httpPost = new HttpPost(httpUrl);
		return sendHttpPost(httpPost);
	}

	/**
	 * get请求
	 * 
	 * @param httpUrl
	 *            地址
	 * @return json
	 */
	public static String sendHttpGet(String httpUrl) {
		// 创建get请求
		HttpGet httpGet = new HttpGet(httpUrl);
		return sendHttpGet(httpGet);
	}

	/**
	 * put
	 * @param httpUrl url
	 * @param params json
	 * @return json
	 */
	public static String sendHttpPutJson(String httpUrl, String params) {
		HttpPut httpPut = new HttpPut(httpUrl);// 创建httpPost
		try {
			// 设置参数
			if (params != null && params.trim().length() > 0) {
				StringEntity stringEntity = new StringEntity(params, "UTF-8");
				stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
				httpPut.setEntity(stringEntity);
			}
		} catch (Exception e) {
		}
		return sendHttpPut(httpPut);
	}
	/**
	 * post
	 * 
	 * @param httpUrl
	 *            地址
	 * @param params
	 *            参数
	 * @return json
	 */
	public static String sendHttpPost(String httpUrl, String params) {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
		try {
			// 设置参数
			if (params != null && params.trim().length() > 0) {
				StringEntity stringEntity = new StringEntity(params, "UTF-8");
				stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
				httpPost.setEntity(stringEntity);
			}
		} catch (Exception e) {
		}
		return sendHttpPost(httpPost);
	}

	/**
	 * post
	 * 
	 * @param httpUrl
	 *            地址
	 * @param maps
	 *            键值
	 * @return json
	 */
	public static String sendHttpPost(String httpUrl, Map<String, String> maps) {
		String parem = convertStringParamter(maps);
		return sendHttpPost(httpUrl, parem);
	}

	/**
	 * post
	 * 
	 * @param httpUrl
	 *            地址
	 * @param paramsJson
	 *            json参数
	 * @return json
	 */
	public static String sendHttpPostJson(String httpUrl, String paramsJson) {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost

		try {
			// 设置参数
			if (paramsJson != null && paramsJson.trim().length() > 0) {
				StringEntity stringEntity = new StringEntity(paramsJson, "UTF-8");
				stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
				httpPost.setEntity(stringEntity);
			}
		} catch (Exception e) {
		}
		return sendHttpPost(httpPost);
	}

	/**
	 * token
	 * 
	 * @param httpUrl
	 *            地址
	 * @param paramsJson
	 *            json参数
	 * @param token
	 *            token信息
	 * @return 结果
	 */
	public static String sendHttpPostJsonWithToken(String httpUrl, String paramsJson, String token) {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost

		try {
			// 设置参数
			if (paramsJson != null && paramsJson.trim().length() > 0) {
				StringEntity stringEntity = new StringEntity(paramsJson, "UTF-8");
				stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
				httpPost.setEntity(stringEntity);
				httpPost.setHeader("X-Auth-Token", token);
			}
		} catch (Exception e) {
		}
		return sendHttpPost(httpPost);
	}

	/**
	 * delete
	 * @param url 地址
	 * @return 结果
	 */
	public static String sendHttpDelete(String url) {
		HttpDelete httpDelete = new HttpDelete(url);
		return sendHttpDelete(httpDelete);
	}

	/**
	 * post
	 * 
	 * @param httpUrl
	 *            地址
	 * @param paramsXml
	 *            xml参数
	 * @return json
	 */
	public static String sendHttpPostXml(String httpUrl, String paramsXml) {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
		try {
			// 设置参数
			if (paramsXml != null && paramsXml.trim().length() > 0) {
				StringEntity stringEntity = new StringEntity(paramsXml, "UTF-8");
				stringEntity.setContentType(CONTENT_TYPE_TEXT_HTML);
				httpPost.setEntity(stringEntity);
			}
		} catch (Exception e) {
		}
		return sendHttpPost(httpPost);
	}

	/**
	 * map转string
	 * 
	 * @param parameterMap
	 *            键值
	 * @return string
	 */
	public static String convertStringParamter(Map parameterMap) {
		StringBuffer parameterBuffer = new StringBuffer();
		if (parameterMap != null) {
			Iterator iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}
				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
		}
		return parameterBuffer.toString();
	}
	public static void main(String[] args) {
		System.out.println(123);
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "123");
		String response = HttpUtils.sendHttpPostJson("http://172.31.10.29:8080/midwarelog", new Gson().toJson(map));
		System.out.println(response);
	}

}
