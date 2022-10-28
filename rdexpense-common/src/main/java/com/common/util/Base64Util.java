package com.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.common.entity.PageData;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



/**
 * Base64加密和解密等
 * @author rdexpense
 */

public class Base64Util {

	/**
	 * <P>
	 * 功能描述:将byte[]编码
	 * </P>
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] encode(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		return (new BASE64Encoder()).encode(bytes).getBytes();
	}

	/**
	 * <P>
	 * 功能描述:将byte[]编码
	 * </P>
	 * 
	 * @param bytes
	 * @return 返回string
	 */
	public static String encodeToStr(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		return (new BASE64Encoder()).encode(bytes);
	}

	/**
	 * <P>
	 * 功能描述:将byte[]解码
	 * </P>
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] decode(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			return new BASE64Decoder().decodeBuffer(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * <P>
	 * 功能描述:base64编码
	 * </P>
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(String s) {
		if (s == null) {
			return null;
		}
		try {
			return (new BASE64Encoder()).encode(s.getBytes("utf-8")).trim();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * <P>
	 * 功能描述:base64解码
	 * </P>
	 * 
	 * @param s
	 * @return
	 */
	public static String decode(String s) {
		if (s == null) {
			return null;
		}
		s = s.trim();
		try {
			byte[] b = new BASE64Decoder().decodeBuffer(s);
			return new String(b, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * <P>
	 * 功能描述:base64解码
	 * </P>
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] decodeToBytes(String s) {
		if (s == null) {
			return null;
		}
		s = s.trim();
		try {
			byte[] b = new BASE64Decoder().decodeBuffer(s);
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 功能描述:base64解码并转为PageData对象
	 * 
	 * @param encodeToken
	 *            加密数据
	 * @return PageData pd对象
	 * @exception Exception
	 */
	public static PageData getTokenInfoPd(String encodeToken) throws Exception {
		String decodeToken = decode(encodeToken);
		if (decodeToken.split("}").length > 0) {
			decodeToken = decodeToken.substring(0, decodeToken.indexOf("}") + 1);
		}
		return JsonUtil.jsonToPageData(decodeToken);
	}

	public static void main(String[] args) {
		try {
			System.out.println(getTokenInfoPd(
					"eyJleHBUaW1lIjozNjAwMDAwLCJ1c2VyTmFtZSI6IjEzNjAxMjg0ODI3IiwiZXhwIjoxNTM1MDIwMzU4LCJ1c2VySWQiOiIzMDAwMDAxOSIsImlhdCI6MTUzNTAxNjc1OH0"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
