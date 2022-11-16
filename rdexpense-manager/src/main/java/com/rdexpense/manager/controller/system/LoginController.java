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
import com.rdexpense.manager.dto.system.login.AuthDTO;
import com.rdexpense.manager.dto.system.login.UserTokenDTO;
import com.rdexpense.manager.service.system.LoginService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
                pd.put("userId",data.getString("userId"));
                pd.put("loginIp", IpAddressUtil.getIpAddress(request));
                logUtil.saveLogData(result.getCode(), 4, "", pd);
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
                logUtil.saveLogData(result.getCode(), 5, "", pd);
            }
        }
    }


}
