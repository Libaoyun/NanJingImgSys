package com.common.util;

/**
 * <pre>
 * 对象功能:运营商API接口验证帮助类
 * 开发人员:rdexpense
 * 创建时间:2018-03-23
 * </pre>
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * <pre>
 * 对象功能:手机号码实名认证(姓名,身份证,手机号)验证帮助类
 * 开发人员:lixin
 * 创建时间:2018-03-23
 * </pre>
 */
public class Mobile3EleAPICheckUtil {

	static Logger logger = Logger.getLogger(Mobile3EleAPICheckUtil.class);

	public static void main(String[] args) {
		String serviceUrl = "https://tapi.ccxcredit.com/data-service/telecom/identity/3mo/t1";
		// serviceUrl =
		// "https://api.ccxcredit.com/data-service/telecom/identity/3mo/t1";
		String account = "shjt_api_ceshi";
		String privateKey = "0c2c6b9a9d5d4593bf0186becb050f88";
		String dataStr = mobile3EleCheck("张三", "123456199001011233", "13800138000",serviceUrl,account,privateKey);
		// String dataStr = mobile3EleCheck("李燕委", "410211198710106049",
		// "18237837933");
		System.out.println(dataStr);
	}

	public static String mobile3EleCheck(String idName, String idCard, String mobile, String serviceUrl, String account,
			String privateKey) {
		try {
			String reqId = getOrderNo();
			String sign = MD5.md5("account" + account + "cid" + idCard + "mobile" + mobile + "name" + idName + "reqId"
					+ reqId + privateKey).toUpperCase();
			System.out.println(sign);
			// 访问URL地址[中文参数必须要进行URLEncoder编码转换,否则会报参数错误;]
			String url = serviceUrl + "?account=" + account + "&name=" + URLEncoder.encode(idName, "UTF-8") + "&cid="
					+ idCard + "&mobile=" + mobile + "&reqId=" + reqId + "&sign=" + sign;
			logger.info("手机号码实名认证<--->URL: " + url);
			String result = readByGet(url);
			logger.info("############Result: " + result);
			return result;
		} catch (Exception e) {
			logger.info("手机号码实名认证失败!!!", e);
			return "";
		}
	}

	/**
	 * 通过GET请求调用url获取结果
	 * 
	 * @param inUrl
	 *            请求url
	 * @throws IOException
	 * @return String 获取的结果
	 */
	public static String readByGet(String inUrl) throws IOException {
		StringBuffer sbf = new StringBuffer();
		String strRead = null;

		// 模拟浏览器
		String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 "
				+ "(KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

		// 连接URL地址
		URL url = new URL(inUrl);
		// 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型,
		// 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 设置连接访问方法及超时参数
		connection.setRequestMethod("GET");
		connection.setReadTimeout(30000);
		connection.setConnectTimeout(30000);
		connection.setRequestProperty("User-agent", userAgent);
		// 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到 服务器
		connection.connect();
		// 取得输入流，并使用Reader读取
		InputStream is = connection.getInputStream();
		// 读取数据编码处理
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		while ((strRead = reader.readLine()) != null) {
			sbf.append(strRead);
			sbf.append("\r\n");
		}
		reader.close();
		// 断开连接
		connection.disconnect();
		return sbf.toString();
	}

	private static AtomicInteger orderNoIncr = new AtomicInteger(1);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	/**
	 * 接入方生成的唯一查询流水号，长度32 位以内
	 */
	public static String getOrderNo() {
		if (orderNoIncr.get() >= 1000) {
			orderNoIncr.set(1);
		}
		return sdf.format(new Date()) + fill();
	}

	/**
	 * 报告编号后三位补0
	 * 
	 * @return
	 */
	public static String fill() {
		String s = String.valueOf(orderNoIncr.getAndIncrement());
		if (s.length() == 1) {
			return "00" + s;
		} else if (s.length() == 2) {
			return "0" + s;
		} else {
			return s;
		}
	}

}
