package com.rdexpense.manager.service.system;


import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.itextpdf.text.pdf.PdfPTable;
import com.rdexpense.manager.dto.system.login.PageJumpVo;
import com.rdexpense.manager.dto.system.login.UserInfoVo;
import com.rdexpense.manager.dto.system.login.UserTokenVo;

public interface LoginService {

    /**
     * 外部用户登录
     * @param pd
     * @return
     */
    PageData externalUserLogin(PageData pd);

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    PageData getUserInfo(String token);

//    /**
//     * 一体化用户退出
//     *
//     * @param token 用户token
//     * @return ResponseEntity<String>
//     */
//    ResponseEntity<String> logout(String token);

    /**
     * 用户退出
     * @param token
     */
    PageData logout(String token);


    /**
     * 一体化平台授权后回调此接口，接口跳转
     * @param authCode
     * @param state
     * @return
     */
    String internalUserCallback(String authCode, String state);

    /**
     * 系统登录页，跳转一体化登陆平台进行授权
     * @param flag
     * @return
     */
    ResponseEntity<PageJumpVo> internalUserAuth(String flag);

    ResponseEntity<UserTokenVo> internalUserLogin(String authCode);

    ResponseEntity<UserInfoVo> getIntegratedUserInfo(String token);
}
