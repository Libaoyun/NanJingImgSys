package com.rdexpense.manager.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.ConstantValUtil;
import com.common.util.HttpGetWithEntity;
import com.common.util.JsonUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 请求外部系统通用方法
 *
 * @author mhl
 */
@Component
public class OutRequestUtil {

    private static final Logger logger = LoggerFactory.getLogger(OutRequestUtil.class);
    private static RedisDao redisDao;


//    @Autowired
//    @Resource(name = "redisDao")
//    private RedisDao redisDao;

    private static OutRequestUtil outRequestUtil;

    @PostConstruct
    public void init() {
        outRequestUtil = this;
    }

    public void setRedisDao(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    public static PageData get(PageData pd, String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            //header中增加token
            String localMacAddr = new MacAddressUtil().getLocalMac();
            String remoteToken = (String) outRequestUtil.redisDao.vGet(localMacAddr);

            headers.add("Authorization", "Bearer " + remoteToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            if (remoteToken == null) {
                throw new MyException("请求一体化的token为空");
            }
            String path = url;
            //获取url
            if (!pd.isEmpty()) {
                path = get(url, pd);
            }

            System.out.println("==========="+pd);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<PageData> response = restTemplate.exchange(path, HttpMethod.GET, entity, PageData.class);

            System.out.println("============="+response);
            if (response.getStatusCode() == HttpStatus.OK) {
                PageData ret = response.getBody();
                return ret;
            } else {
                throw new MyException("获取外部系统数据失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("系统异常");
        }
    }

    public static String getString(PageData pd, String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            //header中增加token
            String remoteToken = (String) outRequestUtil.redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            logger.info("====获取外部系统的remoteToken,参数[{}]====",remoteToken);
            headers.add("Authorization", "Bearer " + remoteToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            if (remoteToken == null) {
                throw new MyException("请求一体化的token为空");
            }
            String path = url;
            //获取url
            if (!pd.isEmpty()) {
                path = get(url, pd);
                logger.info("====获取外部系统的path,参数[{}]====",path);
            }

            RestTemplate restTemplate = new RestTemplate();
            setRestTemplateEncode(restTemplate);
            ResponseEntity<String> response = restTemplate.exchange(path, HttpMethod.GET, entity, String.class);
            logger.info("====获取外部系统的response,参数[{}]====",response);
            if (response.getStatusCode() == HttpStatus.OK) {
                String ret = response.getBody();
                return ret;
            } else {
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
            String remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            headers.add("Authorization", "Bearer " + remoteToken);
            HttpEntity<Object> entity = new HttpEntity<>(obj, headers);
            logger.info("获取远程remoteToken="+remoteToken);
            if (remoteToken == null) {
                throw new MyException("请求一体化的token为空");
            }

            RestTemplate restTemplate = new RestTemplate();
            logger.info("调用远程接口="+url);
            ResponseEntity<PageData> response = restTemplate.exchange(url, HttpMethod.POST, entity, PageData.class);
            logger.info("调用远程接口返回response="+response);
            if (response.getStatusCode() == HttpStatus.OK) {
                PageData ret = response.getBody();
                return ret;
            } else {
                throw new MyException("获取外部系统数据失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("系统异常");
        }
    }

    /**
     * get带body
     *
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
            if (remoteToken == null) {
                throw new MyException("请求一体化的token为空");
            }
            e.setHeader("Authorization", "Bearer " + remoteToken);
            //获取返回值
            CloseableHttpResponse response = httpclient.execute(e);

            if (response.getStatusLine().getStatusCode() == 200) {
                org.apache.http.HttpEntity entity1 = response.getEntity();
                PageData ret = null;
                if (entity1 != null) {
                    String result = EntityUtils.toString(entity1);
                    ret = JsonUtil.jsonToPageData(result);
                }
                response.close();
                return ret;
            } else {
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
     *
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
     *
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

    /**
     * form表单参数调用第三方接口通用方法
     *
     * @param url   地址
     * @param forms form表单参数
     * @return
     */
    public static PageData postVideo(String url, MultiValueMap<String, Object> forms, String token) throws Exception {

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", token);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(forms, headers);

        PageData result = new PageData();
        //获取返回数据
        String body = restTemplate.postForObject(url, httpEntity, String.class);
        if (body != null) {
            PageData pageData = JsonUtil.jsonToPageData(body);
            if (pageData != null) {
                if ("200".equals(pageData.getString("code"))) {
                    result = JsonUtil.jsonToPageData(pageData.getString("data"));
                } else {
                    //请求失败
                    String message = pageData.getString("msg");
                    logger.error(message);
                }
            }
        } else {
            logger.error("请求异常，获取信息异常！");
            throw new Exception("请求异常，获取信息接口异常！");
        }
        return result;
    }


    /**
     * form表单参数调用第三方接口通用方法
     *
     * @param url   地址
     * @param forms form表单参数
     * @return
     */
    public static String postTokenVideo(String url, MultiValueMap<String, Object> forms) throws Exception {

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(forms, headers);

        String accessToken = "";
        //获取返回数据
        String body = restTemplate.postForObject(url, httpEntity, String.class);
        if (body != null) {
            PageData pageData = JsonUtil.jsonToPageData(body);
            if (pageData != null) {
                accessToken = pageData.getString("access_token");
            } else {
                //请求失败
                String message = pageData.getString("msg");
                logger.error(message);
            }
        } else {
            logger.error("请求异常，获取信息异常！");
            throw new Exception("请求异常，获取信息接口异常！");
        }
        return accessToken;
    }

    /**
     * json参数调用第三方接口通用方法
     *
     * @param obj
     * @param url
     * @return
     */
    public static PageData postLiveUrl(Object obj, String url, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            //header中增加token
            headers.add("Authorization", "bearer " + token);
            HttpEntity<Object> entity = new HttpEntity<>(obj, headers);

            if (token == null) {
                throw new MyException("token不能为空！");
            }

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<PageData> response = restTemplate.exchange(url, HttpMethod.POST, entity, PageData.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                PageData ret = response.getBody();
                PageData result = new PageData();
                if (ret != null && ret.getInt("code") == 200) {
                    Map<String, Object> map = (Map<String, Object>) ret.get("data");
                    result.put("url", map.get("url"));
                    result.put("expireTime", map.get("expireTime"));
                }
                return result;
            } else {
                throw new MyException("获取直播流地址信息数据失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        }
    }
}
