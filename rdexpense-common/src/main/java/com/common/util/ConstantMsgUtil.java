package com.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * 功能描述: 系统枚举类值域
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public enum ConstantMsgUtil {
    //成功状态值
    SUCC_STATUS(0, "成功状态值"),

    //失败状态值
    FAIL_STATUS(1, "失败状态值"),

    FAIL_CJ(1, "获取城轨token失败"),

    //失败状态值
    TIMEOUT_STATUS(2, "超时状态值"),

    /**
     * #正确提示信息：以10000开头
     */
    INFO_SUCCESS(10001, "操作成功"),
    INFO_SAVE_SUCCESS(10002, "保存成功"),
    INFO_SUBMIT_SUCCESS(10003, "提交成功"),
    INFO_DELETE_SUCCESS(10004, "删除成功"),
    INFO_QUERY_SUCCESS(10005, "查询成功"),
    INFO_CANCEL_SUCCESS(1006, "撤销成功"),
    INFO_UPLOAD_SUCCESS(10007, "上传成功"),
    INFO_PRINT_SUCCESS(10008, "打印成功"),
    INFO_EXPORT_SUCCESS(10009, "导出成功"),
    INFO_IMPORT_SUCCESS(10010, "导入成功"),
    INFO_CONSULT_SUCCESS(10011, "参照成功"),
    INFO_API_SUCCESS(10012, "调用{0}接口成功"),
    INFO_UPDATE_SUCCESS(10013, "修改成功"),
    INFO_DOWNLOAD_SUCCESS(10014, "下载成功"),
    INFO_BACK_SUCCESS(10015, "撤销成功"),
    INFO_FLOW_SUCCESS(10016, "工作流成功"),
    INFO_APPROVE_SUCCESS(10017, "审批成功"),
    INFO_DISTRIBUTE_SUCCESS(10018, "派发成功"),
    INFO_VIEW_SUCCESS(10019, "预览图片成功"),
    INFO_EXPERIENCEE_SUCCESS(10020, "评论成功"),
    INFO_BINDING_SUCCESS(10021, "企业微信绑定成功"),
    LOGIN_SUCCESS(10022, "登录成功"),
    AUTH_SUCCESS(10023, "授权成功"),
    LOGIN_OUT_SUCCESS(10024, "退出成功"),
    RESET_PASSWORD_SUCCESS(10025, "重置密码成功"),
    INFO_ADD_SUCCESS(10026, "百度云人脸注册成功"),
    INFO_SEND_SUCCESS(10027, "数据发送成功"),
    INFO_LOGIN_SUCCESS(10028, "登陆成功"),


    /**
     * 系统处理错误编码，以20000开头
     */
    ERR_FAIL(20001, "操作失败"),
    ERR_SAVE_FAIL(20002, "保存失败"),
    ERR_SUBMIT_FAIL(20003, "提交失败"),
    ERR_DELETE_FAIL(20004, "删除失败"),
    ERR_QUERY_FAIL(20005, "查询失败"),
    ERR_CANCEL_FAIL(20006, "取回失败"),
    ERR_UPLOAD_FAIL(20007, "上传失败"),
    ERR_PRINT_FAIL(20008, "打印失败"),
    ERR_EXPORT_FAIL(20009, "导出失败"),
    ERR_IMPORT_FAIL(20010, "导入失败"),
    ERR_CONSULT_FAIL(20011, "参照失败"),
    ERR_API_FAIL(20012, "调用接口失败"),
    ERR_DOWNLOAD_FAIL(20014, "下载失败"),
    ERR_UPDATE_FAIL(20015, "更新失败"),
    ERR_TOKEN_FAIL(20016, "token校验失败"),
    ERR_FILENOTEXIT_FAIL(20017, "文件不存在"),
    ERR_BACK_FAIL(20018, "回退失败"),
    ERR_EDIT_FAIL(20019, "当前状态不允许编辑"),
    ERR_DELETE_FAIL2(20020, "当前状态不允许删除"),
    ERR_FLOW_FAIL(20021, "工作流异常"),
    ERR_APPROVE_FAIL(20022, "审批失败"),
    ERR_HRTREE_FAIL(20023, "HR树重复添加"),
    ERR_HRTREE_DEL_FAIL(20024, "该节点不属于HR树，不能删除"),
    ERR_DISTRIBUTE_FAIL(20025, "派发失败"),
    ERR_VIEW_FAIL(20026, "预览图片失败"),
    ERR_TOKEN_EXPIRE(20027, "已过期，请重新登陆"),
    ERR_LOG_SAVE(20028, "日志保存失败"),
    ERR_LOGIN_FAIL(20029, "查询失败，用户可能没有权限"),
    ERR_FLOW_ORG(20030, "工作流未配置"),
    ERR_PERMISSION_FAIL(20031, "不能给自己授权"),
    ERR_PERMISSION_FAIL1(20032, "授权范围超过您的权限"),
    ERR_STOCK_FAIL(20033, "库存不足"),
    ERR_QUOTE_DUP(20034, "存在引用记录已经被使用，请核实"),
    ERR_WORKFLOW_DUP(20035, "该工作流已经存在，请重新选择"),
    ERR_PERMISSION_OUT(20036, "无访问权限，请至一体化申请授权！"),
    ERR_SUBMIT_DETAIL_FAIL(20037, "提交时，明细不能为空"),
    ERR_SUBMIT_FILE_FAIL(20038, "提交时，附件不能为空"),
    ERR_SUBMIT_PICTURE_FAIL(20039, "提交时，图片不能为空"),
    ERR_EXPERIENCEE_SUCCESS(20040, "评论失败"),
    ERR_SIGN_IN_FAIL(20041, "当前状态不可签到"),
    ERR_BINDING_FAIL(20042, "企业微信绑定失败"),
    ERR_FILE_FAIL(20043,"附件上传过多"),
    ERR_NOT_APPROVE(20044,"该状态不能进行审批"),
    ERR_NOT_AUTH_APPROVE(20045,"无权进行审批"),
    ERR_BACK_FAIL2(20046, "当前状态不允许撤销"),
    ERR_SUBMIT_FAIL5(20047, "当前状态不允许提交"),
    ERR_NO_EMPTY(20048, "业务主键ID不能为空"),
    ERR_NOT_DETAIL_FAIL(20049, "当前单据必填项未填不可提交"),
    ERR_WBS_FAIL(20050, "获取WBS失败"),
    LOGIN_OUT_FAIL(20051, "退出失败"),
    TAX_NOT_SAME(20052, "供电安拆含税总价和合同含税总价不相等"),
    ERR_MENU_EXIST(20053, "菜单编码或名称已存在"),
    ERR_ORG_EXIST(20054, "组织名称已存在"),
    ERR_DELETE_FAIL1(20055, "只有待进场的用户，可以删除"),
    RESET_PASSWORD_FAIL(20056, "重置密码失败"),
    ERR_DELETE_RULE_FAIL(20057, "规则已关联考勤对象，请先移除再删除"),
    ERR_DELETE_ORG_FAIL(20057, "组织已关联人员，请先移除人员再删除"),
    ERR_ADD_FAIL(20058, "人脸注册失败"),
    ERR_CODE_EMPTY(20059, "人员编码CODE不能为空"),
    ERR_TOKEN_OVER_FAIL(20060, "会话超时，请重新登录"),
    ERR_FILE_TYPE(20061, "文件格式不正确"),
    ERR_FILE_SHEET(20062, "文件sheet页不正确，请上传正确的模板"),
    ERR_FILE_SIZE(20063, "表格数据为空"),
    ERR_FILE_HEAD(20064, "文件列名错误"),
    ERR_NOT_EMPTY(20065, "数据未清空"),
    ERR_CELL_EMPTY(20066, "第{0}行,{1}值为空，或者参数不正确"),
    ERR_CELL_EMPTYS(20067, "第{0}行,{1}值应小于风险开始（环）"),
    ERR_CELL_EMPTYS1(20068, "第{0}行,{1}值应小于风险结束（环）"),
    ERR_CELL_EMPTYS2(20069, "第{0}行,{1}值应小于风险结束（里程）"),
    ERR_CELL_EMPTYS3(20070, "第{0}行,{1}值应小于总环数"),
    ERR_CELL_EMPTYS4(20071, "第{0}行,{1}值应小于总里程"),
    ERR_SEND_FAIL(20072, "参数发送失败"),
    ERR_TOKEN_LOGIN_FAIL(20073, "您的账号在另一个设备登录"),
    ERR_LOGIN_FAIL1(20074, "登陆失败"),




    /**
     * 报警提示信息：以3000开头
     */
    WAN_NO_EMPTY(30000, "{0}不能为空"),
    WAN_SUPER_LONG(30001, "{0}长度超出范围,最大可输入{1}个字符"),
    WAN_INT_LONG(30002, "{0}整数长度大于{1}位"),
    WAN_FLOAT_LONG(30003, "{0}小数位长度大于{1}位"),
    WAN_DECIMAL_POINT(30004, "{0}包含{1}个小数"),
    WAN_EXISTING(30005, "{0}数据已存在"),
    WAN_NO_EXISTING(30006, "{0}数据不存在"),
    WAN_WRONGFUL(30007, "{0}参数不合法"),
    WAN_TIMEOUT(30008, "登录超时,请重新登录"),
    WAN_DATE_FORMAT(30009, "{0}日期格式不对"),
    WAN_INT_FORMAT(30010, "{0}不是正整数"),
    WAN_EXPORT(30011, "导出结果为空"),
    WAN_DATE_INTERVAL(30012, "进场时间不可大于退场时间"),
    WAN_FLOAT_LONG2(30013, "{0}小数位长度不等于{1}位"),
    WAN_EXPORT_DETAIL_EMPTY(30014, "获取合同内容数据为空，无法导出"),
    CALL_OUT_ORG_WARRING(30015,"调出单位和调入单位不能一样"),
    CALL_OUT_USER_WARRING(30016,"调出人员和调入人员不能为一个人"),
    ERR_NO_EXIT(30017, "查询的数据信息不存在"),
    ERR_PHONE(30018, "电话号码输入不正确"),
    ERR_TAX_RATE(30019, "税率填写不正确"),
    WAN_CELL_LONG(30020, "第{0}行,{1}长度超出范围,最大可输入{2}个字符"),
    WAN_CELL_INT(30021, "第{0}行,{1}整数长度大于{2}位"),
    WAN_CELL_DECIMAL(30022, "第{0}行,{1}应包含{2}个小数"),
    WAN_FLOAT_LONG1(30023, "{0}需保留{1}位小数"),
    WAN_MIN_LONG(30034, "{0}不得少于{1}字"),
    WAN_CELL_MIN_LONG(30035, "第{0}行,{1}至少输入{2}个字"),
    WAN_EXISTING_CODE(30035, "{0}编码数据已存在"),
    WAN_CELL_MIN_LONG(30036, "第{0}行,{1}至少输入{2}个字"),
    private Integer val;

    private String desc;


    //失败状态值
    private Integer failStatus = 1;


    ConstantMsgUtil(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    /**
     * 编码信息
     *
     * @return
     */
    public Integer val() {
        return this.val;
    }

    /**
     * 描述信息
     *
     * @return
     */
    public String desc() {
        return this.desc;
    }

    /**
     * 返回需要匹配单个或多个参数的提示信息,
     *
     * @param desc       String   提示信息
     * @param paramValue String  可为空值，提示信息中对应的参数，如有多个参数需要匹配，参数使用分号(;)进行分割，
     *                   格式：参数1；参数2
     * @return 错误信息
     */
    public static String getProperty(String desc, String paramValue) {
        String[] params = new String[1];
        if (!StringUtils.isEmpty(paramValue)) {
            if (paramValue.contains(";")) {
                params = paramValue.split(";");
            } else {
                params[0] = paramValue.trim();
            }
        } else {
            return desc;
        }

        //从提示信息中进行字符串替换
        String msg = MessageFormat.format(desc, params);
        return msg;
    }
}
