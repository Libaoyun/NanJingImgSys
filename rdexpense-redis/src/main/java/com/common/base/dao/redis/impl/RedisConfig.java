package com.common.base.dao.redis.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * <pre>
 * 对象功能:redis缓存序列化
 * 开发人员:lixin
 * 创建时间:2018-05-15
 * </pre>
 */
@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String REDIS_ADDRESS_HOST;

	@Value("${spring.redis.port}")
	private String REDIS_ADDRESS_PORT;

	@Bean
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		// 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
		Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		serializer.setObjectMapper(mapper);
		
		template.setHashValueSerializer(serializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		
		template.setValueSerializer(serializer);
		// 使用StringRedisSerializer来序列化和反序列化redis的key值
		template.setKeySerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	public RedissonClient getRedisson() {
		String path = "redis://"+REDIS_ADDRESS_HOST+":"+REDIS_ADDRESS_PORT;
		Config config = new Config();
		config.useSingleServer()
				.setAddress(path)
				.setReconnectionTimeout(10000)
				.setRetryInterval(5000)
				.setTimeout(10000)
				.setConnectTimeout(10000);
		return Redisson.create(config);
	}
}
