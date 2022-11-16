package com.rdexpense.manager.imgsys;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.common.util.HttpUtils;
import com.rdexpense.manager.util.MacAddressUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ImgSysAppTokenMgr {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private  String appToken;
    private  Long   expTime = 24*3600L;

    private static String oauth2Scope;
    private static String oauth2GrantType;
    private static String oauthClientId;
    private static String oauth2ClientSecret;
    private static String oauthUrl;


    @Value("${oauth2.scope}")
    public void setOauth2Scope(String scope) {this.oauth2Scope = scope; }

    @Value("${oauth2.grant_type}")
    public void setOauth2GrantType(String grant_type) {this.oauth2GrantType = grant_type; }

    @Value("${oauth2.url}")
    public void setOauth2Url(String url) {this.oauthUrl = url; }

    @Value("${oauth2.client_id}")
    public void setOauth2ClientId(String client_id) {this.oauthClientId = client_id; }

    @Value("${oauth2.secret}")
    public void setOauth2ClientSecret(String client_secret) {this.oauth2ClientSecret = client_secret; }

    public String getAccessToken(RedisDao redisDao) {
        String localMacAddr = new MacAddressUtil().getLocalMac();

        if (null != appToken && appToken.length() > 0) {
            Object value = redisDao.vGet(localMacAddr);
            if ( null != value) {
                return appToken;
            }
        }

        JSONObject obj = getSvrAccessToken();
        redisDao.vSet(localMacAddr, appToken, expTime);

        return appToken;
    }

    public void updateAppToken2Redis(RedisDao redisDao, String token, Long exp) {
        MacAddressUtil macAddressUtil = new MacAddressUtil();
        String localMacAddr = macAddressUtil.getLocalMac();

        if(null == token || token.length() <= 0) {
            JSONObject obj = getSvrAccessToken();
            redisDao.vSet(appToken, localMacAddr, expTime);
        } else {
            redisDao.vSet(token, localMacAddr, exp);
        }
        return;
    }

    private JSONObject getSvrAccessToken() {
        PageData pdParam = new PageData();
        pdParam.put("grant_type", oauth2GrantType);
        pdParam.put("scope", oauth2Scope);
        pdParam.put("client_id", oauthClientId);
        pdParam.put("client_secret", oauth2ClientSecret);

        int num = 0;
        StringBuffer param = new StringBuffer("");
        @SuppressWarnings("unchecked")
        Set<Map.Entry<String, String>> set = pdParam.entrySet();

        for (Map.Entry<String, String> entry : set) {
            num++;
            param.append(entry.getKey() + "=" + entry.getValue());
            if (num < pdParam.size()) {
                param.append("&");
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        String token = httpUtils.sendPost(oauthUrl, param.toString());
        JSONObject jsonObject = JSON.parseObject(token);

        appToken = jsonObject.getString("access_token");

        return jsonObject;
    }

    @Test
    public void run() {
        JSONObject obj = getSvrAccessToken();
        String token = obj.getString("access_token");
    }



}
