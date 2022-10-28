package com.common.util;

/**
 * <pre>
 * 对象功能:发送短信共通类
 * 开发人员:rdexpense
 * 创建时间:2018-05-28
 * </pre>
 */
public class SmsUtil {
	/** 短信调用网关地址 */
	private static final String GATEWAY = "http://commonsms.chinacloudapp.cn:8080/Send.action";
	/** 签名 */
	private static final String SIGN = "E73CDB55D7364181BCC82121AB388AD9";
	/** 参数 */
	private static final String PARAMS = "si.mysign=%s&si.phoneNumber=%s&si.content=%s";

	/** 短信内容 */
	private static final String CONTENT = "尊敬的用户，您的验证码为%s，有效期为60秒，如非本人操作请忽略。";

	/**
	 * 发送短信
	 * 
	 * @param telephone
	 *            手机号.
	 * @param content
	 *            发送内容.
	 * @return boolean
	 */
	public static final boolean send(String telephone, String content) {
		// 拼接参数
		String params = String.format(PARAMS, SIGN, telephone, content);
		// 发送post请求
		return UrlConnectionUtil.sendPost(GATEWAY, params);
	}

	/**
	 * 生成验证码
	 * 
	 * @return
	 */
	public static String createRandomVcode() {
		// 验证码
		String vcode = "";
		for (int i = 0; i < 6; i++) {
			vcode = vcode + (int) (Math.random() * 9);
		}
		return vcode;
	}

	/**
	 * 生成发送内容
	 * 
	 * @param vcode
	 *            验证码.
	 * @return
	 */
	public static String createContent(String vcode) {
		return String.format(CONTENT, vcode);
	}

	/**
	 * 生成发送内容
	 * 
	 * @param vcode
	 *           用户账号.
	 * @param vcode
	 *           内容.
	 * @return
	 */
	public static String createContent(String content, String vcodes) {
		return String.format(content, vcodes);
	}

}
