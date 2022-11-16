package com.rdexpense.manager.imgsys;

import com.common.base.dao.redis.RedisDao;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String appToken;

    private static String monitorPath;

    //private ImgSysAppTokenMgr imgSysAppTokenMgr;
    //private ImgFileCreationMonitor imgFileCreationMonitor;

    @Value("${oauth2.scope}")
    public void setMonitorPath(String monitorPath) {this.monitorPath = monitorPath; }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext ac = event.getApplicationContext();

		/*JedisConfig jedisConfig = ac.getBean(JedisConfig.class);
		JedisPool pool = jedisConfig.redisPoolFactory();
	    DistributedLock lock = new DistributedLock(pool);
		String identifier = lock.lockWithTimeout("resource", 5000, 1000);*/

        //获取RedisDao的实例
        RedisDao redisDao = ac.getBean(RedisDao.class);

        ImgSysAppTokenMgr imgSysAppTokenMgr = new ImgSysAppTokenMgr();

        //Long expTime = 24*3600L;
        //imgSysAppTokenMgr.updateAppToken2Redis(redisDao, "", expTime);

        appToken = imgSysAppTokenMgr.getAccessToken(redisDao);

        ImgFileCreationMonitor imgFileCreationMonitor = new ImgFileCreationMonitor(redisDao);
        imgFileCreationMonitor.setAppToken(appToken);
        //imgFileCreationMonitor.setRedisDao(redisDao);
        imgFileCreationMonitor.beginWatch();
    }
}
