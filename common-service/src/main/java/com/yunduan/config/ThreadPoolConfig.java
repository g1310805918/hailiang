package com.yunduan.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {


    /**
     * 创建线程池
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(2);
        //最大线程数
        executor.setMaxPoolSize(10);
        //缓存队列
        executor.setQueueCapacity(20);
        //允许空闲时间
        executor.setKeepAliveSeconds(60);
        //线程名的前缀
        executor.setThreadNamePrefix("线程池->");
        //对拒绝task的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

}
