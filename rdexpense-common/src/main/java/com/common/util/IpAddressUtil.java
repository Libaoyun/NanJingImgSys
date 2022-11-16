package com.common.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @auther rdexpense
 * @date 2020/10/12 14:53
 * @describe 获取ip工具类
 */
public class IpAddressUtil {

    public static final String IP_UNKNOWN = "unknown";

    public static final String COMMA = ",";

    private static final List<String> POSSIBLE_IP_HEADER = Lists.newArrayList("x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");



    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        for (String ipHeader : POSSIBLE_IP_HEADER) {
            ip = request.getHeader(ipHeader);
            if (!Strings.isNullOrEmpty(ip) && !IP_UNKNOWN.equalsIgnoreCase(ip)) {
                break;
            } else {
                ip = null;
            }
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(COMMA) != -1) {
            ip = ip.substring(0, ip.indexOf(COMMA));
        }
        return ip;
    }

    /**
     * 获取本地ip
     * @return ip
     * @throws UnknownHostException
     * */
    public static String getLocalIP() throws UnknownHostException{
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }

    /**
     * 获取远程ip
     * @return string
     * */
    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (!checkIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    private static boolean checkIP(String ip) {
        return !(ip == null || ip.length() == 0 || "unkown".equalsIgnoreCase(ip)
                || ip.split(".").length != 4);
    }

    @Test
    public void run() throws UnknownHostException{
        System.out.println(getLocalIP());

        String s = "";
        System.out.println("s���ȣ�"+s.length());

    }


    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.contains(":") && ip.substring(ip.lastIndexOf(":") + 1, ip.length()).equals("1")) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
                //logger.info("用localhost进行了访问，取服务器本地ip地址：" + ip);
            } catch (UnknownHostException e) {
//				log.error(e.getMessage(),e);
            }
        }
        if (ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
