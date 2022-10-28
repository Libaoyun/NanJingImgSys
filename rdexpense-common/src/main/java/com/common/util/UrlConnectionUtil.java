package com.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 对象功能:浏览器浏览网页类
 * 开发人员:rdexpense
 * 创建时间:2018-05-25
 * </pre>
 */
@SuppressWarnings("deprecation")
@Component("urlConnectionUtil")
public class UrlConnectionUtil {

	static Logger log = Logger.getLogger(UrlConnectionUtil.class);

	private static int TIMEOUT = 20000;
	private static final String DEFAULT_CHARSET = "UTF-8";

	// http客户端
	private static CloseableHttpClient httpClient;

	// httpclient配置
	private static RequestConfig requestConfig;

	private static Properties pros = new Properties();

	public UrlConnectionUtil() {
		init();
		if (!pros.isEmpty()) {
			String socketTimeout = pros.getProperty("urlConnectionUtil.socketTimeout");
			if (StringUtils.isNotEmpty(socketTimeout)) {
				TIMEOUT = Integer.valueOf(socketTimeout);
			}
		}
		requestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT).build();
	}

	private void init() {
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("config/threadPool.properties");
		try {
			if (inStream != null) {
				pros.load(inStream);
				inStream.close();
			} else {
				System.out.println("config/threadPool.properties没有找到,无法启动项目,请重新配置!");
				System.exit(0);
			}
		} catch (Exception e) {
			System.exit(0);
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 发送http get请求
	 */
	public static String sendHttpGet(String url) {
		CloseableHttpClient client = getHttpClient();

		return sendGet(client, url);
	}

	/**
	 * 发送http post 有参请求 List<NameValuePair> datalist = new ArrayList
	 * <NameValuePair>(); // 构造请求参数数组 data.add(new BasicNameValuePair("apikey",
	 * apikey)); data.add(new BasicNameValuePair("apicode", apicode));
	 * data.add(new BasicNameValuePair("rettype", rettype)); data.add(new
	 * BasicNameValuePair("number_id", number)); data.add(new
	 * BasicNameValuePair("name", name));
	 */
	public static String sendHttpPost(String url, List<NameValuePair> dataList) {
		CloseableHttpClient client = getHttpClient();
		return sendPost(client, url, dataList);
	}

	/**
	 * 发送同步http 无参post请求 #参数以InputStreamEntity形式传入
	 *
	 * @param url
	 *            目的地址
	 * @param infoByteArr
	 *            需要发送的字节数组
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String sendHttpPost(String url, byte[] infoByteArr) throws UnsupportedEncodingException {
		CloseableHttpClient client = getHttpClient();

		// 发送请求
		return sendPost(client, url, infoByteArr);
	}

	/**
	 * sendRequest(发送get请求)
	 *
	 * @param httpClient
	 * @param url
	 *            目的连接
	 * @return String
	 */
	public static String sendGet(HttpClient httpClient, String url) {
		String result = null;
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse response = httpClient.execute(get);
			// 保存请求结果
			result = IOUtils.toString(response.getEntity().getContent(), DEFAULT_CHARSET);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean sendPost(String url, String param) {
		boolean result = true;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";
		try {
			URLEncoder.encode(url, DEFAULT_CHARSET);
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("ContentType", "text/xml;charset=" + DEFAULT_CHARSET);
			conn.setRequestMethod("POST");
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("User-agent", userAgent);
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), DEFAULT_CHARSET));
			out.print(param);
			out.flush();
			try {
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_CHARSET));
			} catch (FileNotFoundException exception) {
				InputStream err = ((HttpURLConnection) conn).getErrorStream();
				if (err == null) {
					throw exception;
				}
				in = new BufferedReader(new InputStreamReader(err));
			}
			String line;
			while ((line = in.readLine()) != null) {
				sbf.append(line);
				sbf.append("\r\n");
				log.info(sbf.toString());
			}
		} catch (Exception e) {
			result = false;
			log.info("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * sendPostRequest(发送http post请求)
	 *
	 * @param httpClient
	 * @param url
	 *            url地址
	 * @param dataList
	 *            参数列表
	 * @return String
	 * @注 List<NameValuePair> datalist = new ArrayList<NameValuePair>(); //
	 *    构造请求参数数组 data.add(new BasicNameValuePair("apikey", apikey));
	 *    data.add(new BasicNameValuePair("apicode", apicode)); data.add(new
	 *    BasicNameValuePair("rettype", rettype)); data.add(new
	 *    BasicNameValuePair("number_id", number)); data.add(new
	 *    BasicNameValuePair("name", name));
	 */
	public static String sendPost(HttpClient httpClient, String url, List<NameValuePair> dataList) {
		String result = null;
		try {
			HttpPost post = new HttpPost(url);

			// 设置post请求参数
			post.setEntity(new UrlEncodedFormEntity(dataList, DEFAULT_CHARSET));
			HttpResponse response = httpClient.execute(post);

			// 保存请求结果
			result = IOUtils.toString(response.getEntity().getContent(), DEFAULT_CHARSET);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 发送无参post请求
	 *
	 * @param httpClient
	 * @param url
	 * @param infoByteArr
	 *            参数值数组
	 * @return
	 */
	public String sendPost(HttpClient httpClient, String url, byte[] infoByteArr) {
		String result = null;
		try {
			HttpPost post = new HttpPost(url);

			// 创建数组实体
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(infoByteArr);
			DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
			InputStreamEntity inputStreamEntity = new InputStreamEntity(dataInputStream);
			// 设置post请求参数
			post.setEntity(inputStreamEntity);
			HttpResponse response = httpClient.execute(post);

			// 保存请求结果
			InputStream in = response.getEntity().getContent();
			result = IOUtils.toString(in, DEFAULT_CHARSET);
			// byte[] inBytes = IOUtils.toByteArray(in);
			// String res = new String(ArrayUtils.subarray(inBytes, 4,
			// inBytes.length), "gbk");
			// ArrayUtils.reverse(inBytes);
			// byte[] errorcodeArr = ArrayUtils.subarray(inBytes, 0, 4);
			// int errcode = ByteBuffer.wrap(errorcodeArr).getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String sendHttpsPost(String url, String header, String xml) {

		CloseableHttpClient httpsClient = getHttpsClient();
		return sendPost(httpsClient, url, header, xml);
	}

	public static String sendPost(HttpClient httpClient, String url, String header, String xml) {
		String result = null;
		try {
			HttpPost post = new HttpPost(url);

			// 设置请求头
			post.setHeader("Authorization", header);

			// 设置post请求参数
			post.setEntity(new StringEntity(xml, DEFAULT_CHARSET));
			HttpResponse response = httpClient.execute(post);

			// 保存请求结果
			result = IOUtils.toString(response.getEntity().getContent(), DEFAULT_CHARSET);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static synchronized CloseableHttpClient getHttpsClient() {
		SSLConnectionSocketFactory sslCSF = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.build();
			sslCSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		CloseableHttpClient httpsClient = HttpClients.custom().setSSLSocketFactory(sslCSF)
				.setDefaultRequestConfig(requestConfig).build();
		return httpsClient;
	}

	/**
	 * @return CloseableHttpClient
	 * @throws @Description:
	 *             得到http client对象
	 */
	private static synchronized CloseableHttpClient getHttpClient() {
		if (null == httpClient) {
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			// Increase max total connection to 200
			cm.setMaxTotal(200);
			// Increase default max connection per route to 20
			cm.setDefaultMaxPerRoute(30);
			// 关闭空闲两分钟的连接
			// cm.closeIdleConnections(120, TimeUnit.SECONDS);

			httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultRequestConfig(requestConfig).build();
		}
		return httpClient;
	}
}
