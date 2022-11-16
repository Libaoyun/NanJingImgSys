package com.rdexpense.manager.filter;

import com.common.base.dao.redis.RedisDao;
import com.common.util.ConstantValUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;


/**
 * 启动监听类
 * @author fkx
 *
 */
@Slf4j
@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static String strClientID;

	private static String strClientSecret;

	private static String tokenUrl;

	//授权外部系统clientId
	@Value("${token.clientId}")
	public void setStrClientID(String strClientID) {
		ApplicationStartup.strClientID = strClientID;
	}

	@Value("${token.clientSecret}")
	public void setStrClientSecret(String strClientSecret) {
		ApplicationStartup.strClientSecret = strClientSecret;
	}

	@Value("${hrToken.url}")
	public void setTokenUrl(String tokenUrl) {
		ApplicationStartup.tokenUrl = tokenUrl;
	}


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext ac = event.getApplicationContext();

		/*JedisConfig jedisConfig = ac.getBean(JedisConfig.class);
		JedisPool pool = jedisConfig.redisPoolFactory();
	    DistributedLock lock = new DistributedLock(pool);
		String identifier = lock.lockWithTimeout("resource", 5000, 1000);*/

		//获取RedisDao的实例
		RedisDao redisDao = ac.getBean(RedisDao.class);
		//分布式锁
		Date date = new Date();
		//定时任务
		timeTask(date,redisDao);

		//lock.releaseLock("resource", identifier);
	}


  private void timeTask(Date date, RedisDao redisDao) {
    try {
      new Timer("testTimer").scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          try {
            Map<String, Object> map = returnToken();
            // 放入redis
            // responseToken的失效时间24小时
            String responseToken = (String) map.get("token");
            int inspire = (int) map.get("inspire");
            redisDao.vSet(ConstantValUtil.REMOTE_TOKEN, responseToken, (long) inspire);
            logger.info(("redis中取到远程的token。。。。。。。" + redisDao.vGet(ConstantValUtil.REMOTE_TOKEN)));
          } catch (Exception e) {
            logger.info("获取一体化的token出错！！！" + e.getMessage());
          }
        }
      }, date, 2 * 3600 * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * 获取调用人事等系统的token
   */
  private Map<String, Object> returnToken() {
    String responseToken = null;
    int inspire = 0;

    String userMsg = strClientID + ":" + strClientSecret;
    String base64Msg = Base64.getEncoder().encodeToString(userMsg.getBytes());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic " + base64Msg);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(tokenUrl + "?grant_type=client_credentials",
      HttpMethod.GET, entity, String.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        JsonNode root = mapper.readTree(response.getBody());
        if (root.has("access_token") && root.has("expires_in")) {
          JsonNode accessToken = root.path("access_token");
          responseToken = accessToken.asText();
          JsonNode expiresIn = root.path("expires_in");// expires_in 失效时间
          inspire = expiresIn.asInt();// 24小时失效
        }
      } catch (Exception e) {
        System.out.print(e.getMessage());
      }

    } else {
      System.out.println(response.getStatusCode());
      System.out.println(response.getHeaders());
      System.out.println(response.getBody());
    }

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("token", responseToken);
    map.put("inspire", inspire);

    return map;
  }


}
