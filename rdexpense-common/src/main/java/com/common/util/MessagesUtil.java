package com.common.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

/**
 * <pre>
 * 对象功能:发送短信共通类
 * 开发人员:rdexpense
 * 创建时间:2018-05-28
 * </pre>
 */
public class MessagesUtil {

	static Logger logger = Logger.getLogger(MessagesUtil.class);

	/** 短信调用网关地址 */
	private static final String GATEWAY = "https://app.cloopen.com:8883/2013-12-26/Accounts/%s/SMS/TemplateSMS?sig=%s";
	/** 账户id */
	private static final String ACCOUNTSID = "8aaf070863c9d21e0163de3e050b0bd4";
	/** 账户令牌 */
	private static final String AUTHTOKEN = "5121d3d73684479d9d020d54d3e99050";
	/** 应用id */
	private static final String APPID = "8aaf070863c9d21e0163de3e05670bda";
	/** 短信提示有效时间 */
	private static final String VALIDTIME = "60";
	/** 验证码模板ID(发送验证码请使用此模板ID) */
	public static final String CODETEMPID = "254997";
	/** 邀请任务模板ID(数加加邀请任务请使用此ID) */
	public static final String INVITETEMPID = "254999";
	/** 入围合作商通知模板ID */
	public static final String INFORMTEMPID = "255115";
	/** 资源导入，短信通知合作商 */
	public static final String RESOURCETEMPID = "255141";

	/**
	 * 发送短信验证码
	 * 
	 * @param telephone
	 *            手机号.
	 * @param vcode
	 *            验证码.
	 * @param templateId
	 *            模板id.
	 * @throws Exception
	 */
	public static final String send(String telephone, String vcode, String templateId) throws Exception {
		String timestamp = DateUtil.getCurrentSeconds();

		// 验证参数
		String SigParameter = ACCOUNTSID + AUTHTOKEN + timestamp;

		// 对验证参数md5加密
		String sign = DigestUtils.md5Hex(SigParameter);

		// 拼接请求地址
		String url = String.format(GATEWAY, ACCOUNTSID, sign);

		String[] datas = new String[] { vcode, VALIDTIME };

		StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
		sb.append("<appId>").append(APPID).append("</appId>").append("<to>").append(telephone).append("</to>")
				.append("<templateId>").append(templateId).append("</templateId>");
		if (datas != null) {
			sb.append("<datas>");
			for (String s : datas) {
				sb.append("<data>").append(s).append("</data>");
			}
			sb.append("</datas>");
		}
		sb.append("</TemplateSMS>").toString();

		// 请求头验证
		String header = ACCOUNTSID + ":" + timestamp;
		// base64编码
		header = Base64.encodeBase64String(header.getBytes("utf-8"));
		String result = UrlConnectionUtil.sendHttpsPost(url, header, sb.toString());
		logger.info("短信接口请求输出结果: " + result);
		return result;
	}

	/**
	 * 发送短信通知
	 * 
	 * @param telephone
	 *            手机号.
	 * @param tempContent
	 *            用于替换短信模板的内容(英文逗号分隔).
	 * @param templateId
	 *            模板id.
	 * @throws Exception
	 */
	public static final void sendInform(String telephone, String tempContent, String templateId) {
		String timestamp = DateUtil.getCurrentSeconds();

		// 验证参数
		String SigParameter = ACCOUNTSID + AUTHTOKEN + timestamp;

		// 对验证参数md5加密
		String sign = DigestUtils.md5Hex(SigParameter);

		// 拼接请求地址
		String url = String.format(GATEWAY, ACCOUNTSID, sign);

		String[] datas = tempContent.split(",");

		StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
		sb.append("<appId>").append(APPID).append("</appId>").append("<to>").append(telephone).append("</to>")
				.append("<templateId>").append(templateId).append("</templateId>");
		if (datas != null) {
			sb.append("<datas>");
			for (String s : datas) {
				sb.append("<data>").append(s).append("</data>");
			}
			sb.append("</datas>");
		}
		sb.append("</TemplateSMS>").toString();

		// 请求头验证
		String header = ACCOUNTSID + ":" + timestamp;
		// base64编码
		try {
			header = Base64.encodeBase64String(header.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(header + ">>>转换字节数组失败", e);
		}

		String result = UrlConnectionUtil.sendHttpsPost(url, header, sb.toString());

		logger.info("短信接口请求输出结果: " + result);

	}

	public static void main(String[] args) throws Exception {
		// send("13488827199", createRandomVcode());
		// send("13488827199", createRandomVcode(), CODETEMPID);
		sendInform("18560721468", "18560721468", RESOURCETEMPID);
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
}
