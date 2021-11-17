package com.common.util;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 18:05
 */
public enum ErrorCodeEnum {
    SYSTEM_EXCEPTION(1000000,"系统异常，请稍后再试"),
    INSERT_USER_EXCEPTION(1000001,"添加用户信息异常"),
    QUERY_USER_EXCEPTION(1000002,"查询用户信息列表异常"),
    BLANK_TOKEN(1000003,"用户token为空"),
    OUT_USER_EXCEPTION(1000004,"获取外部人员信息失败"),
    USER_INFO_EXCEPTION(1000005,"获取用户信息失败"),
    USER_POST_EXCEPTION(1000006,"获取用户岗位失败"),
    USER_DEPARTMENT_EXCEPTION(1000007,"获取部门失败"),
    IDENTITY_NUMBER_EXCEPTION(1000008,"身份证号，手机号已存在"),
    UPDATE_USER_EXCEPTION(1000009,"更新用户信息异常"),
    RESET_PASSWORD_EXCEPTION(1000010,"重置密码异常"),
    UPDATE_PASSWORD_EXCEPTION(1000011,"更新密码异常"),
    DELETE_USER_EXCEPTION(1000012,"删除用户信息异常"),
    QUERY_USER_INFO_EXCEPTION(1000013,"查询用户信息"),
    USER_TOKEN_EXCEPTION(1000014,"用户token异常"),
    USER_LOGIN_EXCEPTION(1000015,"用户名或者密码错误"),
    USER_PASSWORD_EXCEPTION(1000016,"用户密码错误"),
    GET_TOKEN_EXCEPTION(1000017,"获取redis中token异常"),
    GET_OUT_SYSTEM_EXCEPTION(1000018,"获取外部接口异常"),
    GET_HR_TREE_EXCEPTION(1000019,"获取组织树信息异常"),
    ENGINEERING_TREE_EXCEPTION(1000020,"获取工程树信息异常"),
    QUERY_FLOW_INFO_EXCEPTION(1000021,"查询流程信息异常"),
    QUERY_FLOW_ONE_EXCEPTION(1000022,"已存在流程信息"),
    UPDATE_FLOW_EXCEPTION(1000023,"更新流程信息异常"),
    CREATE_FLOW_EXCEPTION(1000024,"新增流程信息异常"),
    GET_USER_TOKEN_EXCEPTION(1000025,"获取用户token信息异常"),
    QUERY_MECHANICAL_EXCEPTION(1000026,"机械设备实力表查询异常"),
    INSERT_MECHANICAL_EXCEPTION(1000027,"添加机械设备实力表信息异常"),
    UPDATE_MECHANICAL_EXCEPTION(1000028,"更新机械设备实力表信息异常"),
    AUTHORIZE_USER_EXCEPTION(1000029,"用户授权异常"),
    DICTIONARY_TREE_EXCEPTION(1000030,"获取数据字典树信息异常"),
    DICTIONARY_INFO_EXCEPTION(1000031,"获取数据字典信息异常"),
    INSERT_DICTIONARY_EXCEPTION(1000032,"添加数据字典枚举值异常"),
    UPDATE_DICTIONARY_EXCEPTION(1000033,"更新数据字典枚举值异常"),
    DELETE_DICTIONARY_EXCEPTION(1000034,"删除数据字典枚举值异常"),
    DEL_MECHANICAL_EXCEPTION(1000035,"删除机械设备实力表信息异常"),
    OPERATIONLOG_INFO_EXCEPTION(1000036,"获取操作日志信息异常"),
    MENU_BUTTON_INFO_EXCEPTION(1000037,"查询路由菜单异常"),
    AUTH_SELF_EXCEPTION(1000038,"不能对自己进行授权"),
    QUERY_MECHANICAL_INFO_EXCEPTION(1000039,"管理号码/资产编号已存在"),
    QUERY_APPROVE_NOTE_EXCEPTION(1000040,"查询审批记录异常"),
    QUERY_ATTACHMENT_EXCEPTION(1000041,"查询附件信息异常"),
    DEL_ATTACHMENT_EXCEPTION(1000042,"删除附件信息异常"),
    DOWNLOAD_ATTACHMENT_EXCEPTION(1000043,"下载附件信息异常"),
    UPLOAD_ATTACHMENT_EXCEPTION(1000044,"上传附件信息异常"),
    AUTH_FAILED(1000045,"授权失败"),
    EDIT_AUTH_FAILED(1000046,"编辑用户授权失败"),
    GET_USER_POSITION_EXCEPTION(1000047,"获取用户岗位信息失败"),
    DATA_HAS_EXIST(1000048,"数据已存在"),
    DIC_ENUM_EXIST(1000049,"字典枚举ID已存在"),


    PARAM_LOSE_EXCEPTION(2000000,"参数异常"),
    AUTHORIZE_EXCEPTION(2000001,"您无访问权限，请联系管理员处理"),
    STRING_OVER_LONG(2000002, "字段长度超出范围")


    ;

    private Integer val;

    private String desc;

    ErrorCodeEnum(Integer val, String desc)
    {
        this.val = val;
        this.desc = desc;
    }

    /**
     * 编码信息
     * @return
     */
    public Integer val()
    {
        return this.val;
    }

    /**
     * 描述信息
     * @return
     */
    public String desc()
    {
        return this.desc;
    }
}
