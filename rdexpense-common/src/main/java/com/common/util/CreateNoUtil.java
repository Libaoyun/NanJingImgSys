package com.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * 对象功能:生成编号工具类
 * 开发人员:rdexpense
 * 创建时间:2018-08-09
 * </pre>
 */
public class CreateNoUtil {

	/**
	 * 日期格式:yyyyMMddHHmmss
	 */
	private static SimpleDateFormat sdfSeconds = DateUtil.sdfSeconds;
	/**
	 * 日期格式:yyyyMMddHHmmssSSS
	 */
	private static SimpleDateFormat sdfMills = DateUtil.sdfMills;
	/**
	 * 日期格式:yyyyMMddHHmmssSSS
	 */
	private static String defaultSixNo = "000000";

	private static AtomicInteger settleNoIncr = new AtomicInteger(1);

	/**
	 * 设置结算单号
	 */
	public static String getSettleNo() {
		if (settleNoIncr.get() >= 100000) {
			settleNoIncr.set(1);
		}
		String noIncr = String.valueOf(settleNoIncr.getAndIncrement());
		String settleNo = defaultSixNo.substring(0, (defaultSixNo.length() - noIncr.length())) + noIncr;
		return sdfSeconds.format(new Date()) + settleNo;
	}

	public static void main(String[] args) {
		String type = "9999";
		System.out.println("000000".substring(0, (6 - type.length())) + type);
	}

	/**
	 * 设置单据编号
	 * @param module 模块名称，例如GZGL
	 * @param inCode 数据库取出最大单据编号，例如GZGL201909250001
	 * 				  若查询出的最大编号的日期是昨天的，那么最后顺序号重头开始计数
	 * @return
	 */
	public static String getRequestCode(String module, String inCode) {
		String outCode="";
		//获取当前八位日期
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());

		if(StringUtils.isBlank(inCode)) {//若传入的单据编号是空，则生成该数据库第一条数据
			outCode=module+date+"0001";
		}else {
			//截取数据库最大编号的日期，若日期是当天，则最后的顺序号直接加1，否则顺序号重新计数
			int first = module.length();
			String inDate = inCode.substring(first, first+8);
			int no = Integer.parseInt(inCode.substring(first+8)) + 1;
			if(inDate.equals(date)) {
				outCode=module+date+formatNo(no);
			}else {
				outCode=module+date+"0001";
			}
		}
		return outCode;
	}

	/**
	 * 格式化数字，不满四位，前面补0,超过四位，不做改变
	 * @param no
	 * @return
	 */
	public static String formatNo(int no) {
		DecimalFormat df=new DecimalFormat("0000");
		return df.format(no);
	}

	/**
	 * 设置管理号码
	 * @param module  标识
	 * @param inCode 数据库取出最大管理号码
	 * 				  若查询出的最大编号的日期是昨天的，那么最后顺序号重头开始计数
	 * @return
	 */
	public static String getManageNumber(String module, String inCode) {
		String outCode="";
		//获取当前八位日期
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(new Date());

		if(StringUtils.isBlank(inCode)) {//若传入的管理号码是空，则生成该数据库第一条数据
			outCode=module+date+"100001";
		}else {
			//截取数据库最大编号的日期，若日期是当天，则最后的顺序号直接加1，否则顺序号重新计数
			int first = module.length();
			String inDate = inCode.substring(first, first+8);
			int no = Integer.parseInt(inCode.substring(first+8)) + 1;
			if(inDate.equals(date)) {
				outCode=module+date+formatNo(no);
			}else {
				outCode=module+date+"10001";
			}
		}
		return outCode;
	}
}
