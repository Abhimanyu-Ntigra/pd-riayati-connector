package com.ntigra.riayati_middleware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean("claimUploadTaskExecutor")
    public ThreadPoolTaskExecutor claimUploadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("claim-upload-");
        executor.initialize();
        return executor;
    }

    @Bean("claimDownloadTaskExecutor")
    public ThreadPoolTaskExecutor claimDownloadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("claim-download-");
        executor.initialize();
        return executor;
    }

    @Bean("eligibilityUploadTaskExecutor")
    public ThreadPoolTaskExecutor eligibilityUploadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("eligibility-upload-");
        executor.initialize();
        return executor;
    }

    @Bean("eligibilityDownloadTaskExecutor")
    public ThreadPoolTaskExecutor eligibilityDownloadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("eligibility-download-");
        executor.initialize();
        return executor;
    }

    @Bean("authorizationUploadTaskExecutor")
    public ThreadPoolTaskExecutor authorizationUploadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("auth-upload-");
        executor.initialize();
        return executor;
    }

    @Bean("authorizationDownloadTaskExecutor")
    public ThreadPoolTaskExecutor authorizationDownloadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("auth-download-");
        executor.initialize();
        return executor;
    }
}