package cn.iocoder.yudao.module.studybuddy.framework.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * StudyBuddy 异步任务配置
 *
 * @author StudyBuddy
 */
@Configuration(proxyBeanMethods = false)
public class StudyBuddyJobConfiguration {

    public static final String STUDYBUDDY_TASK_EXECUTOR = "studybuddyTaskExecutor";

    @Bean(STUDYBUDDY_TASK_EXECUTOR)
    public ThreadPoolTaskExecutor studybuddyTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(100); // 队列容量
        executor.setKeepAliveSeconds(60); // 空闲线程存活时间
        executor.setThreadNamePrefix("studybuddy-task-"); // 线程名称前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略
        executor.initialize();
        return executor;
    }

}
