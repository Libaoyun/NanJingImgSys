package com.common.util;

import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请求外部系统通用方法
 * @author rdexpense
 *
 */
@Component
public class OutRequestUtil {

	@Autowired
	@Resource(name = "redisDao")
	private RedisDao redisDao;
	
	private static OutRequestUtil outRequestUtil;
	
	@PostConstruct
	public void init() {
		outRequestUtil=this;
	}
	
	public static PageData get(PageData pd, String url) {
		try {
            HttpHeaders headers = new HttpHeaders();
            //header中增加token
            String remoteToken = (String) outRequestUtil.redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            headers.add("Authorization", "Bearer " + remoteToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            if(remoteToken == null){
            	throw new MyException("请求一体化的token为空");
            }
            String path =url;
            //获取url
            if(!pd.isEmpty()) {
            	path = get(url, pd);
            }
            
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.ResponseEntity<PageData> response = restTemplate.exchange(path, HttpMethod.GET,entity, PageData.class);
            
            if(response.getStatusCode() == HttpStatus.OK){
            	PageData ret = response.getBody();
            	return ret;
            }else {
            	throw new MyException("获取外部系统数据失败");
            }
		} catch (Exception e) {
			throw new RuntimeException("系统异常");
		}
	}
	
	public static String getString(PageData pd, String url) {
		try {
            HttpHeaders headers = new HttpHeaders();
            //header中增加token
            String remoteToken = (String) outRequestUtil.redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            headers.add("Authorization", "Bearer " + remoteToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            if(remoteToken == null){
            	throw new MyException("请求一体化的token为空");
            }
            String path =url;
            //获取url
            if(!pd.isEmpty()) {
            	path = get(url, pd);
            }
            
            RestTemplate restTemplate = new RestTemplate();
            setRestTemplateEncode(restTemplate);
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.GET,entity, String.class);
            
            if(response.getStatusCode() == HttpStatus.OK){
            	String ret = response.getBody();
            	return ret;
            }else {
            	throw new MyException("获取外部系统数据失败");
            }
		} catch (Exception e) {
			throw new RuntimeException("系统异常");
		}
	}
	
	public static PageData post(Object obj, String url) {
		try {
            HttpHeaders headers = new HttpHeaders();
            //header中增加token
            String remoteToken = (String) outRequestUtil.redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            headers.add("Authorization", "Bearer " + remoteToken);
            HttpEntity<Object> entity = new HttpEntity<>(obj,headers);
            
            if(remoteToken == null){
            	throw new MyException("请求一体化的token为空");
            }
            
            RestTemplate restTemplate = new RestTemplate();
	        org.springframework.http.ResponseEntity<PageData> response = restTemplate.exchange(url, HttpMethod.POST,entity, PageData.class);
            
            if(response.getStatusCode() == HttpStatus.OK){
            	PageData ret = response.getBody();
            	return ret;
            }else {
            	throw new MyException("获取外部系统数据失败");
            }
		} catch (Exception e) {
			throw new RuntimeException("系统异常");
		}
	}
	
	/**
	 * get带body
	 * @param body
	 * @param path
	 * @return
	 */
	public static PageData getWithBody(String body, String path) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGetWithEntity e = new HttpGetWithEntity(path);
			//设置body
		    StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
		    e.setEntity(entity);
            //header中增加token
            String remoteToken = (String) outRequestUtil.redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            if(remoteToken == null){
            	throw new MyException("请求一体化的token为空");
            }
            e.setHeader("Authorization", "Bearer " + remoteToken);
            //获取返回值
            CloseableHttpResponse response = httpclient.execute(e);
            
            if(response.getStatusLine().getStatusCode() == 200){
            	org.apache.http.HttpEntity entity1 = response.getEntity();
            	PageData ret = null;
                if (entity1 != null) {
                    String result = EntityUtils.toString(entity1);
                    ret = JsonUtil.jsonToPageData(result);
                }
                response.close();
            	return ret;
            }else {
            	if (response != null) {
            		response.close();
                }
            	throw new MyException("获取外部系统数据失败");
            }
		} catch (Exception e) {
			throw new RuntimeException("系统异常");
		}
	}
	
	/**
	 * 拼接get的url
	 * @param url
	 * @param pd
	 * @return
	 */
	private static String get(String url, PageData pd) {
		//构建url
		StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        int num = 0;
        @SuppressWarnings("unchecked")
		Set<Map.Entry<String, String>> set = pd.entrySet();
        for (Map.Entry<String, String> entry : set) {
            num++;
            sb.append(entry.getKey() + "=" + entry.getValue());
            if (num < pd.size()) {
                sb.append("&");
            }
        }
        return sb.toString();
	}
	
	/**
	 * 解决RestTemplate如果用String来接收，默认编码是ISO-8859-1；这边设置编码为utf-8
	 * @param restTemplate
	 */
	public static void setRestTemplateEncode(RestTemplate restTemplate) {
	    if (null == restTemplate || ObjectUtils.isEmpty(restTemplate.getMessageConverters())) {
	        return;
	    }

	    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
	    for (int i = 0; i < messageConverters.size(); i++) {
	        HttpMessageConverter<?> httpMessageConverter = messageConverters.get(i);
	        if (httpMessageConverter.getClass().equals(StringHttpMessageConverter.class)) {
	            messageConverters.set(i, new StringHttpMessageConverter(StandardCharsets.UTF_8));
	        }
	    }
	}
}
