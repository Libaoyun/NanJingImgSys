package com.common.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 对象功能:常用工具类
 * 开发人员:rdexpense
 * 创建时间:2018-01-15
 * </pre>
 */
@Component("commonConfigUtil")
public class CommonConfigUtil {

	private Logger logger = Logger.getLogger(this.getClass());

	private static Properties pros = new Properties();

	public CommonConfigUtil() {
		init();
	}

	private void init() {
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("config/threadPool.properties");
		try {
			if (inStream != null) {
				pros.load(inStream);
				inStream.close();
			} else {
				System.out.println("config/threadPool.properties没有找到,无法启动项目,请重新配置!");
				System.exit(0);
			}
		} catch (Exception e) {
			System.exit(0);
			logger.error(e.getMessage(),e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	public static String getProperty(String key) {
		return pros.getProperty(key);
	}

	public static Properties getPros() {
		return pros;
	}

	/** 获取请求地址 **/
	public static String getHostUrl() {
		return getProperty("hostUrl");
	}
}
