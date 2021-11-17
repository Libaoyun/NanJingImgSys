package com.common.base.dao.redis.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class JedisConfig {

	@Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.timeout}")
    private int timeout;
    
    
    
    /*@Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;*/
    
    //@Bean
    public JedisPool redisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //jedisPoolConfig.setMaxIdle(maxIdle);
        //jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        //JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port);
        return jedisPool;
    }
	
	
}
