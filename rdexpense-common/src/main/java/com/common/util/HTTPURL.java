package com.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.common.base.exception.MyException;

/**
 * http工具类
 *
 * @author rdexpense
 */
public class HTTPURL {

	public static String GETCase(String methodUrl, String userid, String username) {
		// String methodUrl = "http://110.32.44.11:8086/sp-test/usertest/1.0/query";
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String line = null;
		try {
			URL url = new URL("https://lc.cr11g.com.cn/default/bpmApi/rest/" + methodUrl);

			connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
			connection.setRequestMethod("GET");// 默认GET请求
			connection.setRequestProperty("userID", userid);
			connection.setRequestProperty("userNAME", username);
			connection.setRequestProperty("Charset", "application/json;charset=UTF-8");
			connection.setConnectTimeout(5000);//设置连接超时时间为5秒
			connection.setReadTimeout(5000);//设置读取数据超时时间为5秒
			connection.connect();// 建立TCP连接
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
				StringBuilder result = new StringBuilder();
				// 循环读取流
				while ((line = reader.readLine()) != null) {
					result.append(line).append(System.getProperty("line.separator"));// "\n"
				}
				return result.toString();
			}
		} catch (java.net.SocketTimeoutException e) {
			e.printStackTrace();
			throw new MyException("调用普元工作流超时，超时时间为5秒");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader!=null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return "";
	}

	public static String POSTCase(String methodUrl, byte[] bs, String userid, String username) {
		// String methodUrl = "http://110.32.44.11:8086/sp-test/usertest/1.0/query";
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String line = null;
		try {
			URL url = new URL("https://lc.cr11g.com.cn/default/bpmApi/rest/" + methodUrl);

			connection = (HttpURLConnection) url.openConnection();// 根据URL生成HttpURLConnection
			connection.setRequestMethod("POST");// 默认GET请求
			connection.setRequestProperty("userID", userid);
			connection.setRequestProperty("userNAME", username);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Charset", "utf-8");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(5000);//设置连接超时时间为5秒
			connection.setReadTimeout(5000);//设置读取数据超时时间为5秒

			connection.connect();// 建立TCP连接
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.write(bs);
			out.flush();
			out.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));// 发送http请求
				StringBuilder result = new StringBuilder();
				// 循环读取流
				while ((line = reader.readLine()) != null) {
					result.append(line).append(System.getProperty("line.separator"));// "\n"
				}
				return result.toString();
			}
		} catch (java.net.SocketTimeoutException e) {
			throw new MyException("调用普元工作流超时，超时时间为5秒");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader!=null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return "";
	}

	public List<Map<String, Object>> jsonArrayToList(Object js) throws JSONException {
		JSONArray jsonarray = new JSONArray(js.toString());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (jsonarray != null && jsonarray.length() > 0) {
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject json = jsonarray.getJSONObject(i);
				Iterator<?> it = json.keys();
				Map<String, Object> map = new HashMap<String, Object>();
				while (it.hasNext()) {
					String key = (String) it.next();
					map.put(key, json.get(key).toString());
				}
				list.add(map);
			}

		}
		return list;

	}

}
