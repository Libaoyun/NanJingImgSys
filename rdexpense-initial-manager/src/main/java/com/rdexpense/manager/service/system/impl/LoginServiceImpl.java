package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.rdexpense.manager.dto.system.login.PageJumpVo;
import com.rdexpense.manager.dto.system.login.UserInfoVo;
import com.rdexpense.manager.dto.system.login.UserTokenVo;
import com.rdexpense.manager.service.flow.FlowService;
import com.rdexpense.manager.service.system.LoginService;
import com.rdexpense.manager.service.system.PermissionService;
import com.rdexpense.manager.util.OutRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static com.common.util.ConstantMsgUtil.LOGIN_OUT_FAIL;
import static com.common.util.ConstantMsgUtil.LOGIN_OUT_SUCCESS;


@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Value("${openid.api.url}")
    private String openidPrefix;

    @Value("${openid.login.redirectURI}")
    private String strRedirectURI;

    @Value("${openid.login.url}")
    private String issuerURL;

    //登录clientId
    @Value("${openid.login.clientId}")
    private String strClientID;

    @Value("${openid.login.clientSecret}")
    private String strClientSecret;

    @Value("${hr.api.url}")
    private String hrPrefix;

    @Value("${hrTree.user.url}")
    private String hrUser;

    @Value("${hrTree.position.path}")
    private String hrPositionPath;

    @Value("${org.code.tree}")
    private String orgCodeTree;

    @Value("${token.time}")
    private long LOGIN_TOKEN_EXPTIME;

    @Value("${openid.login.ourRedirect}")
    private String webRedirect;

    @Value("${openid.appLogin.ourRedirect}")
    private String appRedirect;

    @Autowired
    @Resource(name = "redisDao")
    private RedisDao redisDao;

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;

    @Autowired
    @Resource(name = "FlowService")
    FlowService flowService;

    @Autowired
    private PermissionService permissionService;


    /**
     * 一体化平台授权后回调此接口，接口跳转
     * @param authCode
     * @param state
     * @return
     */
    @Override
    public String internalUserCallback(String authCode, String state) {
        log.info("authCode: [{}[ & state :[{}]", authCode, state);

        if (state.length() < 3 || StringUtils.isBlank(state) || StringUtils.isBlank(authCode)) {
            log.info("内部用户授权回调参数有误");
            return null;
        }

        // 用户在一体化平台登录成功后进入此页面，我们可以取得用户ID信息
        String stateSub = state.substring(0, 3);
        try {
            // 这个要和前面对比验证一下是不是我们发出的
            OIDCProviderMetadata providerMetadata = getOIDCProviderMetadata();
            TokenRequest tokenReq = new TokenRequest(providerMetadata.getTokenEndpointURI(),
                    new ClientSecretBasic(new ClientID(strClientID), new Secret(strClientSecret)),
                    new AuthorizationCodeGrant(new AuthorizationCode(authCode), new URI(openidPrefix + strRedirectURI)));

            //生成Token
            HTTPResponse tokenHTTPResp = tokenReq.toHTTPRequest().send();
            OIDCTokenResponse tokenResponse = OIDCTokenResponse.parse(tokenHTTPResp);
            log.info("token的值{}", tokenResponse.getTokens().getAccessToken().toString());

            Map<String, Object> userMap = getUserName(tokenResponse.getTokens().getAccessToken().toString());
            userMap.put("flag", stateSub);

            //将用户信息塞到redis中 临时token
            String tokenValue = UUID.randomUUID().toString();
            log.info("tokenValue的值[{}],塞入的authCode值[{}]", tokenValue, authCode);

            //用户信息存入redis，后面需要删除

            System.out.println("****************回调用户的authCode: "+authCode+",以及tokenValue: "+tokenValue);
            redisDao.vSet(authCode, tokenValue, (long) 3600);
            redisDao.vSet(tokenValue, userMap, LOGIN_TOKEN_EXPTIME);

            System.out.println("取回authCode:" +redisDao.vGet(authCode));
            System.out.println("取回tokenValue:" +redisDao.vGet(tokenValue));

        } catch (Exception e) {
            log.error("获取用户信息操作异常 ", e);
            return null;
        }

        String url="";
        if ("web".equals(stateSub)) {
            url = openidPrefix + webRedirect + "?authCode=" + authCode;
        }
        if ("app".equals(stateSub)) {
            url = openidPrefix + appRedirect + "?authCode=" + authCode;
        }


        log.info("跳转url=>{}", url);
        System.out.println("******************跳转url"+url);
        return url;
    }


    private OIDCProviderMetadata getOIDCProviderMetadata() throws Exception {
        // 可以缓存内容5分钟
        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(issuerURL, String.class);
        } catch (Exception e) {
            log.error("中铁一体化鉴权异常 [{}]", issuerURL, e);
            throw new MyException("中铁一体化鉴权异常" + issuerURL);
        }

        if (response.getStatusCodeValue() != 200) {
            log.error("一体化响应码 {} , URL:{}", response.getStatusCodeValue(), issuerURL);
            throw new MyException("一体化响应失败");
        }
        return OIDCProviderMetadata.parse(response.getBody());
    }

    /**
     * 根据token获取用户id
     *
     * @param strIdToken token
     * @return Map<String, Object>
     */
    private Map<String, Object> getUserName(String strIdToken) {
        Map<String, Object> map = new HashMap<>();
        try {
            SignedJWT signedJWT = SignedJWT.parse(strIdToken);
            Date exp = signedJWT.getJWTClaimsSet().getDateClaim("exp");
            //用户
            String userName = signedJWT.getJWTClaimsSet().getStringClaim("user_name");
            //组织机构
            String orgName = userName.split("\\|")[0];
            //用户id
            String userId = userName.split("\\|")[1];
            map.put("exp", exp.getTime());
            map.put("userId", userId);
            map.put("orgName", orgName);
        } catch (Exception e) {
            log.error("查询用户信息异常 ", e);
        }
        log.info("授权用户信息{}", map);
        return map;
    }

    /**
     * 系统登录页，跳转一体化登陆平台进行授权
     * @param flag
     * @return
     */
    @Override
    public ResponseEntity<PageJumpVo> internalUserAuth(String flag) {
        //0-web端 ,1-app端
        String flagStr="";
        if("0".equals(flag)){
            flagStr="web";
        }
        if("1".equals(flag)){
            flagStr="app";
        }
        try {
            OIDCProviderMetadata providerMetadata = getOIDCProviderMetadata();
            // 客户端ID,一个业务系统对应一个
            ClientID clientID = new ClientID(strClientID);
            // 客户端的回调URL,即我们业务登录鉴权的URL，这个url要和在注册中心的一致
            //https://cr11csgdshebei.ocloudware.com/openid_connect_login
            URL callback = new URL(openidPrefix + strRedirectURI);
            // 产生随机字符串，保存了后面验证。有效期10秒就可以了。
            String random = flagStr + UUID.randomUUID().toString();
            State state = new State(random);
            // Specify scope
            Scope scope = Scope.parse("openid profile");
            // 构造请求URL，重定向给用户此URL
            AuthenticationRequest req = new AuthenticationRequest(providerMetadata.getAuthorizationEndpointURI(),
                    new ResponseType(ResponseType.Value.CODE), scope, clientID, callback.toURI(), state, new Nonce());
            URI authReqURI = req.toURI();
            // 重定向给用户或者让前端程序处理重定向,url中携带相关参数
            log.info("回调URL:{}", authReqURI.toString());
            return ResponseEntity.success(PageJumpVo.setUrl(authReqURI.toString()), ConstantMsgUtil.AUTH_SUCCESS.desc());
        } catch (Exception e) {
            log.error("生成一体化跳转URL异常 ", e);
            return ResponseEntity.failure(ErrorCodeEnum.SYSTEM_EXCEPTION.val(), ErrorCodeEnum.SYSTEM_EXCEPTION.desc());
        }
    }

    /**
     * 一体化授权token登陆--获取用户信息
     * @param authCode
     * @return
     */
    @Override
    public ResponseEntity<UserTokenVo> internalUserLogin(String authCode) {
        log.info("一体化授权token登录authCode的值{}", authCode);
        System.out.println("****************登陆用户的authCode: "+authCode);
        //获取用户id，获取到之后删除
        try {
            //处理内部用户 --获取用户id，获取到之后删除
            String userTokenValue = (String) redisDao.vGet(authCode);

            System.out.println("用户userTokenValue：  "+userTokenValue);
            if (StringUtils.isBlank(userTokenValue)) {
                return ResponseEntity.failure(ErrorCodeEnum.USER_TOKEN_EXCEPTION.val(), ErrorCodeEnum.USER_TOKEN_EXCEPTION.desc());
            }

            //获取用户id和失效时间
            Map<String, Object> userMap = (Map<String, Object>) redisDao.vGet(userTokenValue);
            System.out.println("用户userMap：  "+userMap);
            // 删除临时token
            redisDao.delete(authCode);

            String userId = MapUtils.getString(userMap, "userId");
            log.info("用户工号[{}]", userId);

            String flag = MapUtils.getString(userMap, "flag");

            //判断账号是否已登陆
            String keys = flag + ":" +ConstantValUtil.LOGIN_TOKEN+"*";
            Set<String> keySet = redisDao.getScan(keys);
            for (String s : keySet){

                String keyName = s.substring(s.lastIndexOf(":"));
                if (keyName.equals(userId)) {
                    if (redisDao.vGet(s) != null) {
                        UserInfoVo userInfoVo = (UserInfoVo) redisDao.vGet(s);
                        if (userInfoVo != null && userInfoVo.getLoginSource() != null && userInfoVo.getLoginSource().equals(flag)) {
                            userInfoVo.setOtherLogin(true);
                            redisDao.vSet(s, userInfoVo, 7200L);
                        }
                    }
                }
            }

            List<String> arrayList = Lists.newArrayList(userId);
            String accessToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            if (StringUtils.isBlank(accessToken)) {
                return ResponseEntity.failure(ErrorCodeEnum.BLANK_TOKEN.val(), ErrorCodeEnum.BLANK_TOKEN.desc());
            }
            List<String> list = getUserPosList(userId, accessToken);
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
                arrayList.addAll(list);
            }


            //在一体化的失效时间基础上，加上一个小时的时间
            long exp = MapUtils.getLong(userMap, "exp") + LOGIN_TOKEN_EXPTIME * 1000;
            //转date类型
            Date expDate = new Date(exp);

            // HR系统获取用户信息 详见接口文档
            org.springframework.http.ResponseEntity<String> response = getInternalUserInfo(userId, accessToken);
            if (response.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.failure(ErrorCodeEnum.USER_INFO_EXCEPTION.val(), ErrorCodeEnum.USER_INFO_EXCEPTION.desc());
            }
            // {"id":142718,"name":"汪婧","gender":2,"catagory":101301,"positionStatus":101401,"order":1}
            log.info("查询一体化用户信息 =>{}", JSON.toJSONString(response.getBody()));
            UserInfoVo sourceVo = JSONObject.parseObject(response.getBody(), UserInfoVo.class);

            //获取用户岗位信息
            //[{"type":3,"id":142686,"name":"部员","code":"0000100011010007702099001","order":1,"mainPosition":true}]
            List<Map<String, Object>> userPos = getUserPos(userId, accessToken);
            log.info("查询用户岗位信息 =>{}", userPos);
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(userPos)) {
                return ResponseEntity.failure(ErrorCodeEnum.GET_USER_POSITION_EXCEPTION.val(), ErrorCodeEnum.GET_USER_POSITION_EXCEPTION.desc());
            }

            // 登录成功将token,设置redis
            String tokenValue = UUID.randomUUID().toString();
            String key = flag + ":" +ConstantValUtil.LOGIN_TOKEN + ":" +tokenValue + ":" +userId;

            UserInfoVo userInfoVo = new UserInfoVo();
            BeanUtils.copyProperties(sourceVo, userInfoVo);
            //1 内部员工 ,0-外部员工
            userInfoVo.setUserFlag(1);
            userInfoVo.setToken(key);
            userInfoVo.setExptime(exp / 1000);
            userInfoVo.setPosition(userPos);
            userInfoVo.setLoginSource(flag);
            userInfoVo.setOtherLogin(false);

            // 将用户信息存入redis
//            redisDao.vSetAt(key, tokenValue, expDate);
            redisDao.vSetAt(key, userInfoVo, expDate);

            // TODO 记录日志
//            logService.saveLoginLog(request, map, ConstantValUtil.LOG_TYPE[0], "登录成功", "23");
            UserTokenVo userTokenVo = new UserTokenVo();
            userTokenVo.setToken(key);
            return ResponseEntity.success(userTokenVo, ConstantMsgUtil.LOGIN_SUCCESS.desc());
        } catch (Exception e) {
            log.error("获取一体化用户token失败", e);
            return ResponseEntity.failure(ErrorCodeEnum.SYSTEM_EXCEPTION.val(), ErrorCodeEnum.SYSTEM_EXCEPTION.desc());
        }
    }

    private List<String> getUserPosList(String userId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // redis 共享accessToken
        HttpEntity<String> entity = new HttpEntity<>(headers);
        //获取用户岗位信息
        //[{"type":3,"id":142686,"name":"部员","code":"0000100011010007702099001","order":1,"mainPosition":true}]
        String hrPath = hrUser + "/" + userId + "/positions";// 142718
        org.springframework.http.ResponseEntity<JSONArray> response2 = restTemplate.exchange(hrPrefix + hrPath, HttpMethod.GET, entity, JSONArray.class, "UTF-8");
        if (response2.getStatusCode() != HttpStatus.OK) {
            log.warn("[{}]查询用户信息失败 url => {} , api token = >{}", userId, hrPath, accessToken);
            return null;
        }

        List<String> pos = new ArrayList<>();
        for (Object object : response2.getBody()) {
            LinkedHashMap positionMap = (LinkedHashMap) object;
            pos.add(MapUtils.getString(positionMap, "id"));
        }
        return pos;
    }

    private org.springframework.http.ResponseEntity<String> getInternalUserInfo(String userId, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // redis 共享
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String path = hrUser + "/" + userId;// 142718
        RestTemplate restTemplate = new RestTemplate();
        OutRequestUtil.setRestTemplateEncode(restTemplate);
        return restTemplate.exchange(hrPrefix + path, HttpMethod.GET, entity, String.class);
    }

    private List<Map<String, Object>> getUserPos(String userId, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // redis 共享accessToken
        HttpEntity<String> entity = new HttpEntity<>(headers);
        //获取用户岗位信息
        //[{"type":3,"id":142686,"name":"部员","code":"0000100011010007702099001","order":1,"mainPosition":true}]
        String hrPath = hrUser + "/" + userId + "/positions";// 142718
        org.springframework.http.ResponseEntity<String> response2;
        try {
            response2 = restTemplate.exchange(hrPrefix + hrPath, HttpMethod.GET, entity, String.class);
        } catch (RestClientException e) {
            log.error("查询用户所有的岗位失败", e);
            return null;
        }

        if (response2.getStatusCode() != HttpStatus.OK) {
            log.warn("[{}]查询用户信息失败 url => {} , api token = >{}", userId, hrPath, accessToken);
            return null;
        }

        String responseText2 = response2.getBody();
        List<Map<String, Object>> listObject = (List<Map<String, Object>>) JSONArray.parse(responseText2);

        //根据岗位获取部门以及单位
        String orgDataStr;
        for (Map<String, Object> positionMap : listObject) {
            int positionId = (int) positionMap.get("id");
            String path3 = hrPositionPath + "/" + positionId;
            org.springframework.http.ResponseEntity<String> response3 = restTemplate.exchange(hrPrefix + path3, HttpMethod.GET, entity, String.class);
            if (response3.getStatusCode() != HttpStatus.OK) {
                log.error("查询部门和单位接口失败");
                return null;
            }
            orgDataStr = response3.getBody();
            PositionUtil.getDepartOrgInfo(positionMap, orgDataStr);
        }
        return listObject;
    }

    /**
     * 外部用户登录
     * @param pd
     * @return
     */
    @Override
    public PageData externalUserLogin(PageData pd) {
        PageData result = new PageData();

        //todo 定义用户登录异常次数字段
        String userLoginExceptionNum = "userLoginExceptionNum:" + pd.getString("userCode");
        if (redisDao.existsKey(userLoginExceptionNum)){
            Integer loginNum = (Integer) redisDao.vGet(userLoginExceptionNum);
            //todo 一个用户名登录失败5次后，要等一个小时后才能再次登录，因为过期时间设置的是1个小时过期
            if (loginNum > 5){
                throw new MyException(ErrorCodeEnum.USER_LOGIN_EXCEPTION_number.desc());
            }
        }

        //此处用户名身份证
        //查管理人员表
//        baseDao.findForObject("UserMapper.findExternalUser", pd);
        System.out.println("======================");
        PageData user = (PageData) baseDao.findForObject("UserMapper.findExternalUser", pd);
        if (user == null) {
            //todo 设置用户登录异常次数字段userLoginExceptionNum，如果没有，则设置此字段，且过期时间为1小时
            redisDao.vSetIncrement(userLoginExceptionNum,(long)1,3600L);
            throw new MyException(ErrorCodeEnum.USER_LOGIN_EXCEPTION.desc());
        }

        //校验是否首次登陆，首次登陆需要重置密码,前提是已经授权的
        result.put("firstLogin", user.getInt("version") == 0);

        //校验用户密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(pd.getString("password"), user.getString("password"));
        if (!matches) {
            //todo 设置用户登录异常次数字段userLoginExceptionNum，如果没有，则设置此字段，且过期时间为1小时
            redisDao.vSetIncrement(userLoginExceptionNum,(long)1,3600L);
            throw new MyException(ErrorCodeEnum.USER_LOGIN_EXCEPTION.desc());
        }

        //获取登陆token
        String userToken = setExternalUserInfos(pd, user);
        result.put("token", userToken);
        return result;

    }


//    /**
//     * 退出系统
//     * @param token 用户token
//     * @return
//     */
//    @Override
//    public ResponseEntity<String> logout(String token) {
//        if (StringUtils.isBlank(token)) {
//            return ResponseEntity.success("用户token不能为空");
//        }
//        try {
//            redisDao.delete(token);
////            logService.saveLoginLog(request, userMap, ConstantValUtil.LOG_TYPE[1], "退出登录", "23");
//            return ResponseEntity.success(LOGIN_OUT_SUCCESS.desc());
//        } catch (Exception e) {
//            log.error("用户退出异常", e);
//            return ResponseEntity.failure(LOGIN_OUT_FAIL.val(), LOGIN_OUT_FAIL.desc());
//        }
//    }


    /**
     * 退出系统
     * @param token
     */
    @Override
    public PageData logout(String token) {
        PageData data = null;
        if(redisDao.existsKey(token)){
            data= (PageData) redisDao.vGet(token);
            if(data == null){
                log.warn("获取redis信息异常：request = [{}]", JSON.toJSONString(token));
                throw new MyException(ErrorCodeEnum.GET_USER_TOKEN_EXCEPTION.desc());
            }

            redisDao.delete(token);
        }else {
            throw new MyException(ErrorCodeEnum.GET_USER_TOKEN_EXCEPTION.desc());
        }

        return data;
    }


    /**
     * 获取一体化用户信息，工程树信息
     * @param token
     * @return
     */
    @Override
    public ResponseEntity<UserInfoVo> getIntegratedUserInfo(String token) {
        log.info("进入获取一体化用户信息getIntegratedUserInfo方法");
        //token为空
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.failure(ErrorCodeEnum.USER_TOKEN_EXCEPTION.val(), ErrorCodeEnum.USER_TOKEN_EXCEPTION.desc());
        }

        try {
            //外部用户
            if (token.startsWith("EXTERNAL")) {
                //查询缓存  --缓存没有数据去数据库查询并再次塞入缓存
                return getExternalUserInfo(token);
            }

            //内部用户
            Object cache = redisDao.vGet(token);
            //token失效,重新授权
            if (cache == null) {
                return ResponseEntity.failure(ErrorCodeEnum.USER_TOKEN_EXCEPTION.val(), ErrorCodeEnum.USER_TOKEN_EXCEPTION.desc());
            }

            UserInfoVo infoVo = (UserInfoVo) cache;
            log.info("内部用户缓存信息 => {}", JSON.toJSONString(infoVo));
            List<String> ids = Lists.newArrayList(String.valueOf(infoVo.getId()));
            //获取access-token （就是API-TOKEN详见ApplicationStartup.class）用于授权查询中铁各种数据
            String accessToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
            if (StringUtils.isBlank(accessToken)) {
                return ResponseEntity.failure(ErrorCodeEnum.BLANK_TOKEN.val(), ErrorCodeEnum.BLANK_TOKEN.desc());
            }
            List<String> list = getUserPosList(String.valueOf(infoVo.getId()), accessToken);
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
                ids.addAll(list);
            }
            JSONObject info = rebuildProjectInfo(ids);
            if (info == null || info.isEmpty()) {
                return ResponseEntity.failure(ErrorCodeEnum.AUTHORIZE_EXCEPTION.val(), ErrorCodeEnum.AUTHORIZE_EXCEPTION.desc());
            }

            infoVo.setScopeTree(info.getJSONArray("projectTree"));
            infoVo.setCurrentProjectId(info.getString("default"));
            return ResponseEntity.success(infoVo, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            log.error("查询内部用户信息异常", e);
            return ResponseEntity.failure(ErrorCodeEnum.SYSTEM_EXCEPTION.val(), ErrorCodeEnum.SYSTEM_EXCEPTION.desc());
        }
    }

    /**
     * 获取外部用户信息
     *
     * @param token 外部用户验证token
     * @return ResponseEntity<UserInfoVo>
     */
    private ResponseEntity<UserInfoVo> getExternalUserInfo(String token) {
        //外部用户获取信息
        try {
            //查询缓存  --缓存没有数据去数据库查询并再次塞入缓存
            Object cache = redisDao.vGet(token);
            if (cache != null) {
                UserInfoVo infoVo = (UserInfoVo) cache;
                JSONObject info = rebuildProjectInfo(Lists.newArrayList(String.valueOf(infoVo.getId())));
                if (info == null || info.isEmpty()) {
                    return ResponseEntity.failure(ErrorCodeEnum.AUTHORIZE_EXCEPTION.val(), ErrorCodeEnum.AUTHORIZE_EXCEPTION.desc());
                }
                infoVo.setScopeTree(info.getJSONArray("projectTree"));
                infoVo.setCurrentProjectId(info.getString("default"));
                return ResponseEntity.success(infoVo, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
            }

            //EXTERNAL:1233:UUID
            String userCode = token.split(":")[1];
            PageData user = (PageData) baseDao.findForObject("UserMapper.findExternalUser",userCode);
            if (user == null) {
                return ResponseEntity.failure(ErrorCodeEnum.AUTHORIZE_EXCEPTION.val(), ErrorCodeEnum.AUTHORIZE_EXCEPTION.desc());
            }

            JSONObject result = rebuildProjectInfo(Lists.newArrayList(String.valueOf(user.getString("id"))));
            if (result == null || result.isEmpty()) {
                return ResponseEntity.failure(ErrorCodeEnum.AUTHORIZE_EXCEPTION.val(), ErrorCodeEnum.AUTHORIZE_EXCEPTION.desc());
            }
            PageData pageData = new PageData();
            pageData.put("userCode",userCode);

            ResponseEntity<UserInfoVo> entity = setExternalUserInfos1(pageData, user);
            UserInfoVo data = entity.getData();
            data.setCurrentProjectId(result.getString("default"));
            data.setScopeTree(result.getJSONArray("projectTree"));
            return entity;
        } catch (Exception e) {
            log.error("查询外部用户信息异常", e);
            return ResponseEntity.failure(ErrorCodeEnum.SYSTEM_EXCEPTION.val(), ErrorCodeEnum.SYSTEM_EXCEPTION.desc());
        }
    }


    /**
     * 设置返回信息
     * @param pd
     * @param user
     * @return
     */
    private ResponseEntity<UserInfoVo> setExternalUserInfos1(PageData pd, PageData user) {
        UserInfoVo userInfoVo = new UserInfoVo();
        //2小时过期
        Long exp = 7200L;
        // 登录成功将token,设置redis
        String tokenKey = "EXTERNAL:" + pd.getString("userCode") + ":" + UUID.randomUUID().toString();
        BeanUtils.copyProperties(user, userInfoVo);
        //用户类型 1 内部员工（一体化） ,0-外部员工
        userInfoVo.setUserFlag(0);
        userInfoVo.setToken(tokenKey);
        userInfoVo.setExptime(exp);
        userInfoVo.setUserName(user.getString("userCode"));
        userInfoVo.setFirstLogin(user.getInt("version") == 0);
        redisDao.vSet(tokenKey, userInfoVo, exp);
        return ResponseEntity.success(userInfoVo, ConstantMsgUtil.INFO_SUCCESS.desc());
    }

    /**
     * 构建用户权限工程树
     *
     * @param userId 用户id
     * @return ResponseEntity
     */
    private JSONObject rebuildProjectInfo(List<String> userId) {
        log.info("授权用户id =>{}", userId);
        JSONObject resp = new JSONObject();
//        List<PageData> scopes = null;
        //查询用户授权信息未授权直接退出，提示无权访问
        List<PageData> scopes = (List<PageData>) baseDao.findForList("AuthorityUserMapper.selectScopeByUserId",userId);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(scopes)) {
            return null;
        }
        log.info("查询用户已授权菜单{}", JSON.toJSONString(scopes));

        //TODO 外部用户流程结束

        ResponseEntity projectTree = permissionService.getProjectTree();
        if (projectTree == null) {
            return null;
        }

        Map<String, Object> set = Maps.newLinkedHashMap();
        for (PageData scope : scopes) {
            set.put(scope.getString("scope_code"),scope.getString("scope_code"));
        }

        JSONArray data = (JSONArray) projectTree.getData();
        log.info("工程树:{}", data);
        String parentId = JSONObject.parseObject(((JSONObject) data.get(0)).toJSONString()).getString("parentId");
        //递归平级
        JSONArray authProject = getProjectArray(data, new JSONArray());
        //根据数据库生成
        JSONArray jsonArray = refactorArray(authProject, set);
        String aDefault = getDefault(jsonArray);
        resp.put("default", aDefault);
        //树华
        JSONArray projectList = treeProjectList(parentId, jsonArray);
        log.info("用户授权工程树{}", projectList);
        resp.put("projectTree", projectList);
        return resp;
    }

    private JSONArray getProjectArray(JSONArray array, JSONArray resp) {
        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            JSONArray children = object.getJSONArray("children");
            if (!org.apache.commons.collections4.CollectionUtils.isEmpty(children)) {
                getProjectArray(children, resp);
                object.put("children", new JSONArray());
            }
            resp.add(object);
        }
        return resp;
    }

    private JSONArray refactorArray(JSONArray array, Map<String, Object> scopes) {
        for (Object o : array) {
            boolean bl = true;
            JSONObject object = (JSONObject) o;
            String scopeCode = object.getString("scopeCode");
            object.put("key", scopeCode);
            object.put("value", scopeCode);
            object.put("title", object.getString("scopeName"));
            for (Map.Entry<String, Object> entry : scopes.entrySet()) {
                String mapKey = entry.getKey();
                if (mapKey.equalsIgnoreCase(scopeCode)) {
                    //不可选中
                    bl = false;
                    break;
                }
            }
            if (bl) {
                object.put("disabled", true);
            } else {
                object.put("disabled", false);
            }
        }

        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            JSONObject object = (JSONObject) next;
            if (object.getBoolean("disabled") && !object.getString("isProject").equals("0")) {
                iterator.remove();
            }
        }

        //公司没有授权
        if (scopes.get(orgCodeTree) == null) {
            for (Map.Entry<String, Object> entry : scopes.entrySet()) {
                String mapKey = entry.getKey();
                for (Object o : array) {
                    JSONObject object = (JSONObject) o;
                    String scopeCode = object.getString("scopeCode");
                    if (mapKey.equalsIgnoreCase(scopeCode)) {
                        object.put("defaultSelected", true);
                        break;
                    }
                }
            }
        } else {
            for (Object res : array) {
                JSONObject obj = (JSONObject) res;
                if (orgCodeTree.equals(obj.getString("scopeCode"))) {
                    obj.put("defaultSelected", true);
                    break;
                }
            }
        }
        return array;
    }

    private String getDefault(JSONArray array) {
        for (Object o : array) {
            JSONObject object = (JSONObject) o;
            if (object.get("defaultSelected") != null) {
                return object.getString("scopeCode");
            }
        }
        return null;
    }

    private JSONArray treeProjectList(String parentId, JSONArray array) {
        JSONArray childProject = new JSONArray();
        for (Object menu : array) {
            JSONObject jsonObject = (JSONObject) menu;
            String id = jsonObject.getString("id");
            String pid = jsonObject.getString("parentId");
            if (parentId.equals(pid)) {
                JSONArray children = treeProjectList(id, array);
                jsonObject.put("children", children);
                childProject.add(menu);
            }
        }
        return childProject;
    }

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    @Override
    public PageData getUserInfo(String token) {
        PageData pageData = new PageData();
        //EXTERNAL:1233:UUID
        String userCode =   token.split(":")[1];
        pageData.put("userCode",userCode);
        PageData user = (PageData) baseDao.findForObject("UserMapper.findExternalUser", pageData);

        PageData data = new PageData();
        if (user == null) {
            throw new MyException(ErrorCodeEnum.AUTHORIZE_EXCEPTION.desc());

        }else {

            //查询部门职务表
            List<PageData> userInfoList = new ArrayList<>();
            userInfoList.add(user);
            List<PageData> postList = (List<PageData>) baseDao.findForList("UserMapper.queryAllPostData", userInfoList);

            if (!CollectionUtils.isEmpty(postList)) {
                data = postList.get(0);
            }

        }
        user.put("token",token);

        //2小时过期
        Long exp = 7200L;
        // 登录成功将token,设置redis
        BeanUtils.copyProperties(user,pageData);
        //用户类型 1 内部员工（一体化） ,0-外部员工
        String tokenKey = user.getString("token");

        pageData.put("token",tokenKey);
        pageData.put("exptime",exp);
        pageData.put("userName",user.getString("userName"));
        pageData.put("firstLogin",user.getInt("version") == 0);

        pageData.put("departmentId",data.getString("departmentCode"));
        pageData.put("department",data.getString("departmentName"));
        pageData.put("postId",data.getString("postCode"));
        pageData.put("post",data.getString("postName"));

        List<String> codeList = new ArrayList<>();
        codeList.add(user.getString("userCode"));
        List<PageData> authList = (List<PageData>) baseDao.findForList("UserMapper.queryAuthDatasByUserId",codeList);
        codeList.clear();
        if(StringUtils.isNotBlank(pageData.getString("postId"))){
            for(String s : pageData.getString("postId").split(",")){
                codeList.add(s);
            }

        }


 //       List<PageData> authList = (List<PageData>) baseDao.findForList("UserMapper.queryAuthData",pageData);
        if (!CollectionUtils.isEmpty(codeList)){
            List<PageData> postAuthList = (List<PageData>) baseDao.findForList("UserMapper.queryAuthDatas",codeList);
            authList.addAll(postAuthList);
        }

        if (!CollectionUtils.isEmpty(authList)){
            pageData.put("companyId",authList.get(0).getString("companyId"));
            pageData.put("organizationList",authList);
            pageData.put("organizationId",authList.get(0).getString("organizationId"));
            pageData.put("organizationName",authList.get(0).getString("organizationName"));
        } else {
            throw new MyException("该用户没有权限，请联系管理员");
        }

        if(redisDao.existsKey(tokenKey)){
            PageData data1 = (PageData) redisDao.vGet(tokenKey);
            if(data == null){
                return null;
            }

            pageData.put("userCode",data1.getString("userCode"));

        }

        redisDao.vSet(tokenKey, pageData, exp);

        return pageData;
    }


    /**
     * 设置返回信息
     * @param pd
     * @param user
     * @return
     */

    private String setExternalUserInfos(PageData pd, PageData user) {

        PageData pageData = new PageData();
        //2小时过期
        Long exp = 7200L;
        // 登录成功将token,设置redis
        //  String tokenKey ="EXTERNAL:"+userName+":"+ UUID.randomUUID().toString();
        String tokenKey ="EXTERNAL:"+pd.getString("userCode")+":"+ UUID.randomUUID().toString();
        BeanUtils.copyProperties(user,pageData);

        pageData.put("token",tokenKey);
        pageData.put("exptime",exp);
        pageData.put("userCode",user.getString("userCode"));
        pageData.put("userName",user.getString("userName"));
        // FIXME 这里新增了用户部门信息
        pageData.put("departmentId",user.getString("departmentCode"));
        pageData.put("department",user.getString("departmentName"));
        pageData.put("firstLogin",user.getInt("version") == 0);


        redisDao.vSet(tokenKey, pageData, exp);

        return tokenKey;
    }




}
