package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.IpAddressUtil;
import com.common.util.PropertyUtil;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import com.rdexpense.manager.dto.system.login.*;
import com.rdexpense.manager.service.system.LoginService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import java.io.IOException;

/**
 * 登录接口
 * @author rdexpense
 *
 */
@RestController
@RequestMapping("/login")
@Api(value = "用户登录",tags = {"用户登录"})
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private LogUtil logUtil;

    /**
     * 一体化平台授权后回调此接口，接口跳转
     *
     * @param authCode 授权code
     * @param state    授权state
     */
    @GetMapping("/openid_rdexpense_login")
    @ApiOperation(value="一体化平台授权回调", notes="<b><font color=\"red\">前端忽略这个接口,中铁授权回调</b>")
    public void internalUserCallback(@ApiParam(value = "一体化授权code", required = true) @RequestParam(value = "code", defaultValue = "") String authCode,
                                     @ApiParam(value = "一体化授权state", required = true) @RequestParam(value = "state", defaultValue = "") String state,
                                     HttpServletResponse response) throws IOException {
        response.sendRedirect(loginService.internalUserCallback(authCode,state));
    }

    /**
     * 系统登录页，跳转一体化登陆平台进行授权
     *
     * @param flag 0-web端 ,1-app端
     * @return ResponseEntity
     */
    @GetMapping(value = "/openid")
    @ApiOperation(value="跳转一体化登陆平台进行授权", notes="内部用户登录授权")
    public ResponseEntity<PageJumpVo> internalUserAuth(@RequestParam(value = "flag")
                                                       @ApiParam(value = "flag 0-web端 ,1-app端", required = true)  String flag) {
        return loginService.internalUserAuth(flag);
    }


    /**
     * 内部用户登录，前端使用临时授权token（即一体化授权token）登录
     *
     * @return ResponseEntity<Object>
     */
    @PostMapping(value = "/internalUser",consumes="application/x-www-form-urlencoded")
    @ApiOperation(value="一体化授权token登录", notes="一体化授权token登录")
    public ResponseEntity<UserTokenVo> internalUserLogin(@ApiParam(value = "临时授权码") @FormParam("authCode") @RequestParam String authCode) {
        System.out.println("一体化授权token登录： "+authCode);
        return loginService.internalUserLogin(authCode);
    }


    /**
     * 获取一体化用户信息，工程树信息
     *
     * @return ResponseEntity<UserInfoVo>
     */
    @PostMapping(value = "/getIntegratedUserInfo")
    @ApiOperation(value="获取一体化用户信息，工程树信息", notes="获取一体化用户信息，工程树信息")
    public ResponseEntity<UserInfoVo> getIntegratedUserInfo() {

        String token = this.getUserToken("token");
        try {
            ResponseEntity result =loginService.getIntegratedUserInfo(this.getUserToken("token"));
            return result;
        } catch (Exception e) {
            logger.error("获取一体化用户信息，工程树信息失败,request=[{}]", token);
            throw new MyException(ConstantMsgUtil.ERR_QUERY_FAIL.desc(), e);
        }
    }

    /**
     * 外部用户登录
     * @param authDto 请求对象
     * @return ResponseEntity<UserTokenVo>
     */
    @PostMapping(value = "/externalUser")
    @ApiOperation(value="用户登录", notes="用户登录")
    public ResponseEntity<UserTokenDTO> externalUserLogin(AuthDTO authDto) {
        PageData pd = this.getLoginParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("userCode"), "用户名",64);
        CheckParameter.stringLengthAndEmpty(pd.getString("password"), "密码",64);

        ResponseEntity result = null;
        PageData data = null;
        try {
            data = loginService.externalUserLogin(pd);
            result = ResponseEntity.success(data, ConstantMsgUtil.LOGIN_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("用户登录失败,request=[{}]", pd);
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_LOGIN_FAIL1.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_LOGIN_FAIL1.desc(), e);
        } finally {
            if(data != null && data.getString("token") != null){
                HttpServletRequest request = this.getRequest();
                PageData pageData = loginService.getUserInfo(data.getString("token"));
                pd.put("loginIp", IpAddressUtil.getIpAddress(request));
                pageData.put("loginIp", IpAddressUtil.getIpAddress(request));
                logUtil.saveLogData(result.getCode(), 4, "", pageData);
            }
        }
    }

    /**
     * 获取用户信息
     *
     * @return ResponseEntity<UserInfoVo>
     */
    @PostMapping(value = "/getUserInfo")
    @ApiOperation(value="获取用户信息", notes="获取用户信息")
    public ResponseEntity<UserInfoDTO> getUserInfo() {

        String token = this.getUserToken("token");
        try {
            PageData data = loginService.getUserInfo(token);
            ResponseEntity result = ResponseEntity.success(PropertyUtil.pushData(data, UserInfoDTO.class, ConstantMsgUtil.INFO_QUERY_SUCCESS.desc()));
            return result;
        } catch (Exception e) {
            logger.error("获取用户信息失败,request=[{}]", token);
            throw new MyException(ConstantMsgUtil.ERR_QUERY_FAIL.desc(), e);
        }
    }

    /**
//     * 一体化用户退出登陆
//     *
//     * @return
//     */
//    @PostMapping(value = "/logout")
//    @ApiOperation(value="用户退出", notes="用户退出")
//    public ResponseEntity<String> logout() {
//        return loginService.logout(this.getUserToken("token"));
//    }

    public String getUserToken(String key) {
        return getRequest().getHeader(key);
    }

    /**
     * 用户退出登陆
     *
     * @return
     */
    @PostMapping(value = "/logout")
    @ApiOperation(value="用户退出", notes="用户退出")
    public ResponseEntity logout() {
        String token = this.getUserToken("token");
        ResponseEntity result = null;
        PageData data = null;
        try {
            data = loginService.logout(token);
            result = ResponseEntity.success(null, ConstantMsgUtil.LOGIN_OUT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            logger.error("退出系统失败,request=[{}]", token);
            result = ResponseEntity.failure(ConstantMsgUtil.LOGIN_OUT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.LOGIN_OUT_FAIL.desc(), e);
        } finally {
            if(data != null){
                HttpServletRequest request = this.getRequest();
                PageData pd = new PageData();
                pd.put("userId",data.getString("id"));
                pd.put("userName",data.getString("userName"));
                pd.put("loginIp", IpAddressUtil.getIpAddress(request));
                logUtil.saveLogData(result.getCode(), 5, "", data);
            }
        }
    }


}
