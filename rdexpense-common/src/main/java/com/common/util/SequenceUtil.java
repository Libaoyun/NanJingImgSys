package com.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis实现流水号的生成
 */
@Slf4j
@Component
public class SequenceUtil {

    /**
     * 流水号key全局唯一
     */
    private static final String SEQUENCE_NO_KEY = "serial:no:auto";
    private static final String PREFIX = "YFHT";

    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        SequenceUtil.redisTemplate = redisTemplate;
    }


    /**
     * 生成序列号，全局唯一
     * @return
     */
    public static String generateSerialNo() {
        RedisAtomicLong entityIdCounter;
        long increment = 0;
        try {
            // redis里对应的key没有值 这里默认值为0，
            // 如果redis里的key有值直接使用redis的值操作
            entityIdCounter = new RedisAtomicLong(SEQUENCE_NO_KEY, redisTemplate.getConnectionFactory());
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
        if(importantKey.length()<4) {
            importantKey = "0000".substring(0,4-importantKey.length())+importantKey;
        }
        return String.format("%s%s%s",PREFIX, FastDateFormat.getInstance("yyyyMMdd").format(new Date()),importantKey);
    }


    /**
     * 生成序列号，每个模块唯一，增加传参
     * @param module
     * @return
     */
    public static String generateSerialNo(String module) {
        RedisAtomicLong entityIdCounter;
        long increment = 0;
        try {
            // redis里对应的key没有值 这里默认值为0，
            // 如果redis里的key有值直接使用redis的值操作
            entityIdCounter = new RedisAtomicLong(module, redisTemplate.getConnectionFactory());
            //这里先睡眠500ms
            Thread.sleep(500);
            //获取上次递增的值并且+1为本次新的流水号，这里返回的是本次递增的值
            increment = entityIdCounter.incrementAndGet();
            log.info("SequenceUtil->redis+1后最大编号", increment);
        } catch (Exception e) {
            log.error("redis设置失败", e);
            return null;
        }

        //设置第二天的凌晨00：00为失效期
        entityIdCounter.expireAt(getExpireDate());
        //补四位,缺失的位置用0补位
        String importantKey = String.valueOf(increment);
        if (importantKey.length() < 4) {
            importantKey = "0000".substring(0, 4 - importantKey.length()) + importantKey;
            log.info("SequenceUtil->单据编号后四位", importantKey);
        }
        log.info("SequenceUtil->生成的最大单据编号", String.format("%s%s%s", module, FastDateFormat.getInstance("yyyyMMdd").format(new Date()), importantKey));
        return String.format("%s%s%s", module, FastDateFormat.getInstance("yyyyMMdd").format(new Date()), importantKey);
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
