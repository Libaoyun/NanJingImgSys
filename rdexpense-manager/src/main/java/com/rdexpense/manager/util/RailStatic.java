package com.rdexpense.manager.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.common.entity.PageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author rdexpense
 * @create 2020/5/14
 * @description 登录的数据权限信息
 */
public class RailStatic {
    private static final ThreadLocal<String> USERID = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new TransmittableThreadLocal<>();
    private static final ThreadLocal<Integer> USERFLAG = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> IP = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> COMPANYID = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> DEPARTMENT = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> DEPARTMENTID = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> POST = new TransmittableThreadLocal<>();
    private static final ThreadLocal<String> POSTID = new TransmittableThreadLocal<>();
    private static final List<ThreadLocal> THREAD_LOCAL_LIST = new ArrayList<>();

    static {
        THREAD_LOCAL_LIST.add(USERID);
        THREAD_LOCAL_LIST.add(USERNAME);
        THREAD_LOCAL_LIST.add(USERFLAG);
        THREAD_LOCAL_LIST.add(IP);
        THREAD_LOCAL_LIST.add(COMPANYID);
        THREAD_LOCAL_LIST.add(DEPARTMENT);
        THREAD_LOCAL_LIST.add(DEPARTMENTID);
        THREAD_LOCAL_LIST.add(POST);
        THREAD_LOCAL_LIST.add(POSTID);
        THREAD_LOCAL_LIST.add(IP);
    }

    public static String getUserId() {
        return USERID.get();
    }

    public static void setUserId(String userId) {
        USERID.set(userId);
    }

    public static String getUserName() {
        return USERNAME.get();
    }

    public static void setUserName(String userName) {
        USERNAME.set(userName);
    }

    public static Integer getUserFlag() {
        return USERFLAG.get();
    }


    public static void setIp(String ip) {
        IP.set(ip);
    }

    public static String getIp() {
        return IP.get();
    }



    public static String getCompanyId() {
        return COMPANYID.get();
    }

    public static void setCompanyId(String companyId) {
        COMPANYID.set(companyId);
    }

    public static String getDepartment() {
        return DEPARTMENT.get();
    }

    public static void setDepartment(String department) {
        DEPARTMENT.set(department);
    }

    public static String getDepartmentId() {
        return DEPARTMENTID.get();
    }

    public static void setDepartmentId(String departmentId) {
        DEPARTMENTID.set(departmentId);
    }

    public static String getPost() {
        return POST.get();
    }

    public static void setPost(String post) {
        POST.set(post);
    }

    public static String getPostId() {
        return POSTID.get();
    }

    public static void setPostId(String post) {
        POSTID.set(post);
    }
    public static void clearAll() {
        for (ThreadLocal threadLocal : THREAD_LOCAL_LIST) {
            threadLocal.remove();
        }

    }


}
