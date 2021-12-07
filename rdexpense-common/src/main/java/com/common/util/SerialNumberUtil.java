package com.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 基于redis实现序号的生成
 */
@Slf4j
@Component
public class SerialNumberUtil {


    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        SerialNumberUtil.redisTemplate = redisTemplate;
    }

    /**
     * 获取每天导出excel的序号
     * @param key 为不同模块的自定义编码 确保在项目中唯一
     * @return
     */
    public static String generateSerialNo(String key) {
        RedisAtomicLong entityIdCounter;
        long increment = 0;
        try {
            // redis里对应的key没有值 这里默认值为0，
            // 如果redis里的key有值直接使用redis的值操作
            entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
            //这里先睡眠500ms
            Thread.sleep(500);
            //获取上次递增的值并且+1为本次新的流水号，这里返回的是本次递增的值
            increment = entityIdCounter.incrementAndGet();
        } catch (Exception e) {
            log.error("redis设置失败",e);
            return null;
        }
        //设置第二天的凌晨00：00为失效期
        entityIdCounter.expireAt(getExpireDate());
        //补四位,缺失的位置用0补位
        String  importantKey=String.valueOf(increment);
        if(importantKey.length()<3) {
            importantKey = "0000".substring(0,4-importantKey.length())+importantKey;
        }
        return importantKey;
    }

    private static Date getExpireDate() {
        Date today=new Date();
        Calendar cs=Calendar.getInstance();
        cs.setTime(today);
        //次日凌晨过期
        cs.add(Calendar.DAY_OF_MONTH,1);
        cs.set(Calendar.HOUR_OF_DAY,0);
        cs.set(Calendar.MINUTE,0);
        cs.set(Calendar.SECOND,0);
        return cs.getTime();
    }
}
