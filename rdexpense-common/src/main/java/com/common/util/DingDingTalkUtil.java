package com.common.util;

import com.alibaba.fastjson.JSONObject;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import com.taobao.api.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author rdexpense
 * @date 2020/9/1 16:39
 * @description 钉钉考勤工具类
 * 参考文档：https://ding-doc.dingtalk.com/doc#/serverapi3/yq9wuc
 */
public class DingDingTalkUtil {
    public static final Logger logger = LoggerFactory.getLogger(DingDingTalkUtil.class);

    /**
     * 获取 access_token
     *
     * @param appKey    应用的 appKey
     * @param appSecret 应用的 appSecret
     * @return access_token
     */
    public static String getDingDingAccessToken(String appKey, String appSecret) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey(appKey);
            req.setAppsecret(appSecret);
            req.setHttpMethod("GET");
            OapiGettokenResponse rsp = client.execute(req);
            if ("0".equals(JSONObject.parseObject(rsp.getBody()).getString("errcode"))) {
                return JSONObject.parseObject(rsp.getBody()).get("access_token").toString();
            } else {
                logger.error("获取token失败，appKey = {}, appSecret = {}", appKey, appSecret);
                throw new MyException("获取token 失败");
            }
        } catch (ApiException e) {
            logger.error("获取token失败，appKey = {}, appSecret = {}", appKey, appSecret);
        }
        return null;
    }

    /**
     * @param accessToken
     * @return 获取部门列表
     */
    public static List<PageData> getDepartList(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
            OapiDepartmentListRequest req = new OapiDepartmentListRequest();
            req.setHttpMethod("GET");
            OapiDepartmentListResponse rsp = client.execute(req, accessToken);
            if ("0".equals(JSONObject.parseObject(rsp.getBody()).getString("errcode"))) {
                String concat = "[".concat(rsp.getBody().concat("]"));
                return JsonUtil.jsonToPageDataList(concat);
            } else {
                logger.error("获取部门列表 失败，accessToken = {} ", accessToken);
                throw new MyException("获取部门列表 失败");
            }
        } catch (ApiException e) {
            logger.error("获取部门列表 失败，accessToken = {} ", accessToken);
        }
        return null;
    }

    /**
     * @param accessToken
     * @param deptId      部门id
     * @return 取部门的成员 id 集合
     */
    public static List<PageData> getUserIds(String accessToken, String deptId) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getDeptMember");
            OapiUserGetDeptMemberRequest req = new OapiUserGetDeptMemberRequest();
            req.setDeptId(deptId);
            req.setHttpMethod("GET");
            OapiUserGetDeptMemberResponse rsp = client.execute(req, accessToken);
            if ("0".equals(JSONObject.parseObject(rsp.getBody()).getString("errcode"))) {
                String concat = "[".concat(rsp.getBody().concat("]"));
                return JsonUtil.jsonToPageDataList(concat);
            } else {
                logger.error("获取部门的成员 id 失败，accessToken = {} ", accessToken);
                throw new MyException("获取部门的成员 id 失败");
            }
        } catch (ApiException e) {
            logger.error("获取部门的成员 id 失败，accessToken = {} ", accessToken);
        }
        return null;
    }

    /**
     * 查询人员在时间段内的考勤结果
     *
     * @param accessToken
     * @param dateFrom    开始时间 形如: "2018-09-10 00:00:00"
     * @param dateTo      结束时间  形如: "2018-09-11 00:00:00"
     * @param userIds     用户 id 数组
     * @return
     */
    public static List<PageData> getUserAttendResult(String accessToken, String dateFrom, String dateTo, String[] userIds) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/attendance/list");
        OapiAttendanceListRequest request = new OapiAttendanceListRequest();
        request.setWorkDateFrom(dateFrom);
        request.setWorkDateTo(dateTo);
        request.setUserIdList(Arrays.asList(userIds));
        request.setOffset(0L);
        request.setLimit(50L);
        OapiAttendanceListResponse rsp = null;
        try {
            rsp = client.execute(request, accessToken);
            if ("0".equals(JSONObject.parseObject(rsp.getBody()).getString("errcode"))) {
                String concat = "[".concat(rsp.getBody().concat("]"));
                return JsonUtil.jsonToPageDataList(concat);
            } else {
                logger.error("获取成员考勤结果 失败，accessToken = {} , dateFrom = {}, dateTo = {}, userIds = {}",
                        accessToken, dateFrom, dateTo, userIds);
                throw new MyException("获取成员考勤结果 失败");
            }
        } catch (ApiException e) {
            logger.error("获取成员考勤结果 失败，accessToken = {} , dateFrom = {}, dateTo = {}, userIds = {}",
                    accessToken, dateFrom, dateTo, userIds);
        }
        return null;
    }


    /**
     * 获取企业考勤报表列
     *
     * @param accessToken
     * @return
     */
    public static List<PageData> getColumns(String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/attendance/getattcolumns");
            OapiAttendanceGetattcolumnsRequest req = new OapiAttendanceGetattcolumnsRequest();
            OapiAttendanceGetattcolumnsResponse rsp = client.execute(req, accessToken);
            if ("0".equals(JSONObject.parseObject(rsp.getBody()).getString("errcode"))) {
                String concat = "[".concat(rsp.getBody().concat("]"));
                return JsonUtil.jsonToPageDataList(concat);
            } else {
                logger.error("获取企业考勤报表列失败，accessToken = {} ", accessToken);
                throw new MyException("获获取企业考勤报表列 失败");
            }
        } catch (ApiException e) {
            logger.error("获取企业考勤报表列失败，accessToken = {} ", accessToken);
        }
        return null;
    }


    /**
     * @param userIds      用户id  多个用户用逗号分割
     * @param columnIdList 要查询的考勤列表的id
     * @param sTime        开始时间
     * @param eTime        结束时间
     * @param accessToken
     * @return 获取智能考勤报表的列值
     */
    public static List<PageData> getColumnVals(String userIds, String columnIdList, String sTime, String eTime, String accessToken) {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/attendance/getcolumnval");
            OapiAttendanceGetcolumnvalRequest req = new OapiAttendanceGetcolumnvalRequest();
            req.setUserid(userIds);
            req.setColumnIdList(columnIdList);
            req.setFromDate(StringUtils.parseDateTime(sTime));
            req.setToDate(StringUtils.parseDateTime(eTime));
            OapiAttendanceGetcolumnvalResponse rsp = client.execute(req, accessToken);
            if ("0".equals(JSONObject.parseObject(rsp.getBody()).getString("errcode"))) {
                String concat = "[".concat(rsp.getBody().concat("]"));
                return JsonUtil.jsonToPageDataList(concat);
            } else {
                logger.error("获取智能考勤报表的列值 失败，accessToken = {} ", accessToken);
                throw new MyException("获取智能考勤报表的列值 失败");
            }
        } catch (ApiException e) {
            logger.error("获取智能考勤报表的列值 失败，accessToken = {} ", accessToken);
        }
        return null;
    }


}
