package com.common.util;

import java.security.MessageDigest;

/**
 * <pre>
 * 对象功能:MD5处理类
 * 开发人员:rdexpense
 * 创建时间:2018-01-15
 * </pre>
 */
public class MD5 {

	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			char str[];
			byte strTemp[] = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte md[] = mdTemp.digest();
			int j = md.length;
			str = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(md5("31119@qq.com" + "123456"));
		System.out.println(md5("mj1"));
	}
}
