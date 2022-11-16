package com.rdexpense.manager.service.system;


import com.common.entity.PageData;
import com.itextpdf.text.pdf.PdfPTable;

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

    /**
     * 用户退出
     * @param token
     */
    PageData logout(String token);


    void getTest(String tokenKey);
}
