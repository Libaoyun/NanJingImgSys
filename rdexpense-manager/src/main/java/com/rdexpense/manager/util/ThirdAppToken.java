package com.rdexpense.manager.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.common.util.HttpUtils;
import com.rdexpense.manager.service.system.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

//@Service
@Slf4j
@Component
//@Configuration
public class ThirdAppToken {

    private Long exp = 7200L;
    private Long half_exp = 3600L;

    private String appToken = "";

//    @Autowired
//    @Resource(name = "redisDao")
//    private RedisDao redisDao;

    @Autowired
    private LoginService loginService;

    public boolean updateToken() {
        if (appToken.length() <= 0){
            JSONObject jsonObject = getAccessToken();
            appToken = jsonObject.getString("access_token");
            if (appToken.length() <= 0) {
                return false;
            }
        }

        String tokenKey ="access_token:"+appToken;

//        loginService.getTest(tokenKey);

//        MacAddressUtil macAddressUtil = new MacAddressUtil();
//        String localMacAddr = macAddressUtil.getMacAddr();
//
//        boolean result = redisDao.vSet(tokenKey, localMacAddr, exp);
//
//        Object obj = redisDao.vGet(tokenKey);
//        Long expTime = redisDao.getExpire(tokenKey);

        return false;
    }

    private JSONObject getAccessToken() {
        String url = "http://localhost:1001/rdexpense/oauth/token";

        PageData pd = new PageData();
        pd.put("grant_type","client_credentials");
        pd.put("scope","select");
        pd.put("client_id","mctech");
        pd.put("client_secret","JaPEdO9X0JE0Qb5JFsTlLkNlx3rqi9sCCjU5104J");

        int num = 0;
        StringBuffer param = new StringBuffer("");
        @SuppressWarnings("unchecked")
        Set<Map.Entry<String, String>> set = pd.entrySet();

        for (Map.Entry<String, String> entry : set) {
            num++;
            param.append(entry.getKey() + "=" + entry.getValue());
            if (num < pd.size()) {
                param.append("&");
            }
        }

        HttpUtils httpUtils = new HttpUtils();
        String token = httpUtils.sendPost(url, param.toString());
        JSONObject jsonObject = JSON.parseObject(token);
        return jsonObject;
    }
}
