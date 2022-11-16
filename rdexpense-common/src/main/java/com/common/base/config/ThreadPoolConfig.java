package com.common.base.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.common.util.ThreadPoolConfigUtil;


/**
 * 线程池配置类
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
	private static Logger logger = Logger.getLogger(ThreadPoolConfig.class);

	@Bean(name = "asyncServiceExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		// 设置核心线程数
		threadPool.setCorePoolSize(
				Integer.parseInt(ThreadPoolConfigUtil.getProperty("async.executor.thread.core.pool.size")));
		// 设置最大线程数
		threadPool
				.setMaxPoolSize(Integer.parseInt(ThreadPoolConfigUtil.getProperty("async.executor.thread.max.pool.size")));
		// 线程池所使用的缓冲队列
		threadPool.setQueueCapacity(
				Integer.parseInt(ThreadPoolConfigUtil.getProperty("async.executor.thread.queue.capacity")));
		// 等待任务在关机时完成--表明等待所有线程执行完
		threadPool.setWaitForTasksToCompleteOnShutdown(true);
		// 等待时间 （默认为0，此时立即停止），并没等待xx秒后强制停止
		threadPool.setAwaitTerminationSeconds(60);
		// 线程名称前缀
		threadPool.setThreadNamePrefix(ThreadPoolConfigUtil.getProperty("async.executor.thread.name.prefix"));
		// 设置拒绝策略rejection-policy：当pool已经达到max size的时候，不在新线程中执行任务，而是有调用者所在的线程来执行
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		logger.info("线程池创建成功");
		return threadPool;
	}
}
