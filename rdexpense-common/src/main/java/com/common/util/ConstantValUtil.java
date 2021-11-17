package com.common.util;


import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 功能描述: 系统常用量值域
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public class ConstantValUtil {

    //附件上传目录
    public static final String BUCKET_PRIVATE = "private1";

    // 调用人事、机构等接口需要的token
    public static final String REMOTE_TOKEN = "urban_rail_access_token";

    // 登录模块token的超时时间
    public static final long LOGIN_TOKEN_EXPTIME = 3600000;

    //默认授权菜单权限menuCode
    public static final Set<String> DEFAULT_MENU_SET = Sets.newHashSet("2","4","5","6","7","16");
    //授权按钮默认授权
    public static final String DEFAULT_AUTH_BTN = "a10001,a10002,a10003,a10004";



    // 登录模块需要的token
    public static final String LOGIN_TOKEN = "urbanRailToken";

    // 人员状态：DICT10141001-已进场, DICT10141002-已退场, DICT10141003-待进场
    public static final String[] USER_STATUS_CODE = {"DICT10141001", "DICT10141002", "DICT10141003"};

    // 人员状态：DICT10141001-已进场, DICT10141002-已退场, DICT10141003-待进场
    public static final String[] USER_STATUS_NAME = {"已进场", "已退场", "待进场"};

}
