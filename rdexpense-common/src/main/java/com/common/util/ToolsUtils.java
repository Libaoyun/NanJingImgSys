package com.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 对象功能:常用工具类
 * 开发人员:rdexpense
 * 创建时间:2018-01-15
 * </pre>
 */
public class ToolsUtils {

	/**
	 * 保留两位小数，如果不满两位小数，补0;(注：1.1=1.10,1.155=1.15,1.156=1.16)
	 */
	public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
	/**
	 * 保留两位小数，如果不满两位小数，返回原值;(注：1.1=1.1,1.155=1.15,1.156=1.16)
	 */
	public static final DecimalFormat decimalFormat2 = new DecimalFormat("#.##");
	/**
	 * 保留整数
	 */
	public static final DecimalFormat decimalFormat3 = new DecimalFormat("0");
	/**
	 * 保留整数
	 */
	public static final DecimalFormat decimalFormat4 = new DecimalFormat("#");

	public static final Pattern regex = Pattern
			.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");

	public static final Pattern regexNew = Pattern
			.compile("^1\\d{10}$");

	/**
	 * 生成一个4位数的模板ID
	 * 
	 * @return
	 */
	public static String getTemplateId() {
		Random r = new Random();
		return String.valueOf(r.nextInt(9000) + 1000);
	}

	private static AtomicInteger dataNoIncr = new AtomicInteger(1);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static AtomicInteger pageNoIncr = new AtomicInteger(1);

	private static AtomicInteger clusterIdIncr = new AtomicInteger(1);

	/**
	 * 设置数据集Id
	 */
	public static String getClusterId() {
		if (clusterIdIncr.get() >= 1000) {
			clusterIdIncr.set(1);
		}
		return sdf1.format(new Date()) + fillClusterId();
	}

	/**
	 * 数据集Id后三位补0
	 * 
	 * @return
	 */
	public static String fillClusterId() {
		String s = String.valueOf(clusterIdIncr.getAndIncrement());
		if (s.length() == 1) {
			return "00" + s;
		} else if (s.length() == 2) {
			return "0" + s;
		} else {
			return s;
		}
	}

	/**
	 * 设置上传页面编号
	 */
	public static String getUploadPageNo() {
		if (pageNoIncr.get() >= 1000) {
			pageNoIncr.set(1);
		}
		return sdf1.format(new Date()) + fill();
	}

	/**
	 * 上传页面后三位补0
	 * 
	 * @return
	 */
	public static String fill() {
		String s = String.valueOf(pageNoIncr.getAndIncrement());
		if (s.length() == 1) {
			return "00" + s;
		} else if (s.length() == 2) {
			return "0" + s;
		} else {
			return s;
		}
	}

	/**
	 * 设置任务数据上传编号
	 */
	public static String getDataUploadNo() {
		if (dataNoIncr.get() >= 1000) {
			dataNoIncr.set(1);
		}
		return sdf.format(new Date()) + fill0();
	}

	/**
	 * 报告编号后三位补0
	 * 
	 * @return
	 */
	public static String fill0() {
		String s = String.valueOf(dataNoIncr.getAndIncrement());
		if (s.length() == 1) {
			return "00" + s;
		} else if (s.length() == 2) {
			return "0" + s;
		} else {
			return s;
		}
	}

	/**
	 * 随机生成六位数验证码
	 * 
	 * @return
	 */
	public static int getRandomNum() {
		Random r = new Random();
		return r.nextInt(900000) + 100000;// (Math.random()*(999999-100000)+100000)
	}

	/**
	 * 检测字符串是否不为空(null,"","null")
	 * 
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean notEmpty(String s) {
		return s != null && !"".equals(s) && !"null".equals(s);
	}

	/**
	 * 检测字符串是否为空(null,"","null")
	 * 
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s) || "null".equals(s);
	}

	/**
	 * 字符串转换为字符串数组
	 * 
	 * @param str
	 *            字符串
	 * @param splitRegex
	 *            分隔符
	 * @return
	 */
	public static String[] str2StrArray(String str, String splitRegex) {
		if (isEmpty(str)) {
			return null;
		}
		return str.split(splitRegex);
	}

	/**
	 * 用默认的分隔符(,)将字符串转换为字符串数组
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static String[] str2StrArray(String str) {
		return str2StrArray(str, ",\\s*");
	}

	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，日期转字符串
	 * 
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String date2Str(Date date) {
		return date2Str(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date) {
		if (notEmpty(date)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Date();
		} else {
			return null;
		}
	}

	/**
	 * 按照参数format的格式，日期转字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date, String format) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 把时间根据时、分、秒转换为时间段
	 * 
	 * @param StrDate
	 */
	public static String getTimes(String StrDate) {
		String resultTimes = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now;
		try {
			now = new Date();
			Date date = df.parse(StrDate);
			long times = now.getTime() - date.getTime();
			long day = times / (24 * 60 * 60 * 1000);
			long hour = (times / (60 * 60 * 1000) - day * 24);
			long min = ((times / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long sec = (times / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

			StringBuffer sb = new StringBuffer();
			// sb.append("发表于：");
			if (hour > 0) {
				sb.append(hour + "小时前");
			} else if (min > 0) {
				sb.append(min + "分钟前");
			} else {
				sb.append(sec + "秒前");
			}
			resultTimes = sb.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultTimes;
	}

	/**
	 * 往文件里的内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @param content
	 *            写入的内容
	 */
	public static void writeFile(String fileP, String content) {
		String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../"; // 项目路径
		filePath = (filePath.trim() + fileP.trim()).substring(6).trim();
		if (filePath.indexOf(":") != 1) {
			filePath = File.separator + filePath;
		}
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath), "utf-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 往文件里的内容（ClassResource下）
	 * 
	 * @param filePath
	 *            文件路径
	 * @param content
	 *            写入的内容
	 */
	public static void writeFileCR(String fileP, String content) {
		String filePath = getClassResources() + fileP;
		try {
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(filePath), "utf-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber) {
		boolean flag = false;
		try {
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 验证手机号码是11位
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumberLength(String mobileNumber) {
		boolean flag = false;
		try {
			Matcher matcher = regexNew.matcher(mobileNumber);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 读取txt里的单行内容
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static String readTxtFile(String fileP) {
		try {
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../"; // 项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if (filePath.indexOf(":") != 1) {
				filePath = File.separator + filePath;
			}
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					return lineTxt;
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件,查看此路径是否正确:" + filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return "";
	}

	/**
	 * 读取txt里的全部内容
	 * 
	 * @param fileP
	 *            文件路径
	 * @param encoding
	 *            编码
	 * @return
	 */
	public static String readTxtFileAll(String fileP, String encoding) {
		StringBuffer fileContent = new StringBuffer();
		try {
			String filePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../"; // 项目路径
			filePath = filePath.replaceAll("file:/", "");
			filePath = filePath.replaceAll("%20", " ");
			filePath = filePath.trim() + fileP.trim();
			if (filePath.indexOf(":") != 1) {
				filePath = File.separator + filePath;
			}
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					fileContent.append(lineTxt);
					fileContent.append("\n");
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件,查看此路径是否正确:" + filePath);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return fileContent.toString();
	}

	/**
	 * 读取ClassResources某文件里的全部内容
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static String readFileAllContent(String fileP) {
		StringBuffer fileContent = new StringBuffer();
		try {
			String encoding = "utf-8";
			File file = new File(getClassResources() + fileP);// 文件路径
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); // 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					fileContent.append(lineTxt);
					fileContent.append("\n");
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件,查看此路径是否正确:" + fileP);
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
		}
		return fileContent.toString();
	}

	/**
	 * 获取classpath2
	 *
	 * @return
	 */
	public static String getClassResources() {
		String path = (String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")))
				.replaceAll("file:/", "").replaceAll("%20", " ").trim();
		if (path.indexOf(":") != 1) {
			path = File.separator + path;
		}
		return path;
	}

	/** 生成8位邀请码 **/
	public static String invitingCodemethod() {
		String[] strArr = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "B", "C", "D", "E", "F", "G", "H",
				"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		StringBuilder codeStr = new StringBuilder();// 定义变长字符串
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
			codeStr.append(strArr[random.nextInt(36)]);
		}
		return codeStr.toString();
	}

	/** 获取指定随机数集合 **/
	public static ArrayList<Integer> appointRandom(int total, int number) {
		// total：总数量， number：需要随机数数量
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < number; i++) {
			Random random = new Random();
			int nextInt = random.nextInt(total);
			if (!list.contains(nextInt)) {
				list.add(nextInt);
			} else {
				number++;
			}
		}
		return list;
	}

	/*
	 * 过滤标签如例子
	 * 
	 * @taskReq 函标签的字段 例子：<p>我是任务要求</p>
	 **/
	public static String regularFiltrationLabel(String taskReq) {
		Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(taskReq);
		String taskReqStr = matcher.replaceAll("");
		return taskReqStr;
	}
}
