package com.common.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.common.entity.RespEntity;

/**
 * <pre>
 * 对象功能:BaseUtil帮助类
 * 开发人员:rdexpense
 * 创建时间:2018-05-15
 * </pre>
 */
public class BaseUtil {

    protected Logger logger = Logger.getLogger(this.getClass());

    /**
     * 调用接口获取数据(GET请求)
     *
     * @param param
     * @param uri
     * @return String
     */
    private String get(Map<String, String> param, String uri) {
        String dataJson = "";
        if (!param.isEmpty()) {
            String url = buildUrl(param, uri);
            logger.info("GET请求地址: " + url);
            if (StringUtils.isNotEmpty(url)) {
                dataJson = UrlConnectionUtil.sendHttpGet(url);
                logger.info("GET请求结果: " + dataJson);
            }
        }
        return dataJson;
    }

    /**
     * 构建访问地址
     *
     * @param param
     * @param url
     * @return String
     */
    private String buildUrl(Map<String, String> param, String url) {
        StringBuffer sb = new StringBuffer(url);
        sb.append("?");
        int num = 0;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            num++;
            sb.append(entry.getKey() + "=" + entry.getValue());
            if (num < param.size()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * httpGet请求
     *
     * @param parametersMap(包含url地址)
     * @return ResponseEntity
     */
    public RespEntity sendHttpGet(Map<String, String> parametersMap) {
        RespEntity respEntity = new RespEntity();
        String url = parametersMap.get("url");
        try {
            parametersMap.put("rettype", "json");
            parametersMap.remove("url");
            // 通过get请求获取数据信息
            String dataStr = this.get(parametersMap, url);
            respEntity = parseResultData(respEntity, dataStr);
        } catch (Exception e) {
            respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
            respEntity.setMsg(ConstantMsgUtil.FAIL_STATUS.desc());
            logger.error("请求接口[" + url + "]错误~", e);
        }
        return respEntity;
    }

    /**
     * httpGet请求
     *
     * @param url
     * @param parametersMap
     * @return ResponseEntity
     */
    public RespEntity sendHttpGet(String url, Map<String, String> parametersMap) {
        parametersMap.put("url", url);
        return sendHttpGet(parametersMap);
    }

    /**
     * httpPost请求
     *
     * @param url
     * @param paramLst
     * @return ResponseEntity
     */
    public RespEntity sendHttpPost(String url, List<NameValuePair> paramLst) {
        RespEntity respEntity = new RespEntity();
        try {
            // 把host地址和API接口地址进行拼接
            url = CommonConfigUtil.getHostUrl() + url;
            // 通过get请求获取数据信息
            logger.info("POST请求地址: " + url);
            String dataStr = UrlConnectionUtil.sendHttpPost(url, paramLst);
            logger.info("GET请求结果: " + dataStr);
            respEntity = parseResultData(respEntity, dataStr);
        } catch (Exception e) {
            respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
            respEntity.setMsg(ConstantMsgUtil.FAIL_STATUS.desc());
            logger.error("请求接口[" + url + "]错误~", e);
        }
        return respEntity;
    }
    /**
     * httpPost请求 
     *
     * @param url
     * @param paramLst
     * @return ResponseEntity
     */
    public RespEntity sendHttpPostOther(String url, List<NameValuePair> paramLst) {
        RespEntity respEntity = new RespEntity();
        try {
            // 把host地址和API接口地址进行拼接
            //url = CommonConfigUtil.getHostUrlRelease() + url;
            // 通过get请求获取数据信息
            logger.info("POST请求地址: " + url);
            String dataStr = UrlConnectionUtil.sendHttpPost(url, paramLst);
            logger.info("GET请求结果: " + dataStr);
            respEntity = parseResultData(respEntity, dataStr);
        } catch (Exception e) {
            respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
            respEntity.setMsg(ConstantMsgUtil.FAIL_STATUS.desc());
            logger.error("请求接口[" + url + "]错误~", e);
        }
        return respEntity;
    }
    /**
     * httpPost请求
     *
     * @param url
     * @param paramLst
     * @return ResponseEntity
     */
    public String httpPost(String url, List<NameValuePair> paramLst) {
        String dataStr = null;
        try {
            // 通过get请求获取数据信息
            logger.info("POST请求地址: " + url);
            dataStr = UrlConnectionUtil.sendHttpPost(url, paramLst);
            logger.info("GET请求结果: " + dataStr);
        } catch (Exception e) {
            logger.error("请求接口[" + url + "]错误~", e);
        }
        return dataStr;
    }

    /**
     * 解析返回的数据
     *
     * @param respEntity
     * @param dataStr
     * @return ResponseEntity
     */
    private RespEntity parseResultData(final RespEntity respEntity, final String dataStr) {
        if (StringUtils.isNotEmpty(dataStr)) {
            JSONObject dataJson = JSONObject.parseObject(dataStr);
            // 如果当前返回数据中包含status,说明当前接口返回数据失败,那么直接把提示放入msg中;否则就把data数据放入responseJson中返回到前台;
            String status = dataJson.getString("status");
            String msg = dataJson.getString("msg");
            respEntity.setStatus(status);
            respEntity.setMsg(msg);
            Object responseObject = dataJson.get("responseObject");
            Object responseList = dataJson.get("responseList");
            Object responsePageData = dataJson.get("responsePageData");
            Object responsePageInfo = dataJson.get("responsePageInfo");
            if (responseObject != null) {
                respEntity.setResponseObject(responseObject);
            }
            if (responseList != null) {
                respEntity.setResponseList(JsonUtil.jsonToList(responseList));
            }
            if (responsePageData != null) {
                respEntity.setResponsePageData(JsonUtil.jsonToPageData(responsePageData));
            }
            if (responsePageInfo != null) {
                respEntity.setResponsePageInfo(JsonUtil.jsonToPageInfo(responsePageInfo));
            }
        } else {
            logger.info("请求接口返回数据为空~");
            respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
            respEntity.setMsg(ConstantMsgUtil.FAIL_STATUS.desc());
        }
        return respEntity;
    }
}
