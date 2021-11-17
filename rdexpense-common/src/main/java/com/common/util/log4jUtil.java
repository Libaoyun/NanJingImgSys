package com.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 对象功能:加载配置log4j日志
 * 开发人员:rdexpense
 * 创建时间:2018-05-11
 * </pre>
 */
@Component
@Order
public class log4jUtil {

	private static Properties pros = new Properties();

	public log4jUtil() {
		init();
	}

	private void init() {
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("config/log4j.properties");
		try {
			if (inStream != null) {
				pros.load(inStream);
				// 装入log4j配置信息
				PropertyConfigurator.configure(pros);
				inStream.close();
			} else {
				System.out.println("config/log4j.properties没有找到,无法启动项目,请重新配置!");
				System.exit(0);
//				throw new Exception("config/log4j.properties没有找到,请重新配置!");
			}
		} catch (Exception e) {
			System.exit(0);
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
