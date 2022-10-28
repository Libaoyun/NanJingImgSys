/**
 * 
 */
package com.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Auther: rdexpense
 * @Date: 2021/2/4 14:02
 * @Description: 生成UUID工具类
 */
public class UUIDUtil {

    /**
     *  java.util.UUID 有可能重复
     *  java.util.UUID + 日期时间 生成唯一ID ，length长度:46
     * @return
     */
    public static String getUUID() {
    	String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");	
		String str = sdf.format(new Date());
		uuid = uuid+str;
		return uuid;
    }

}
