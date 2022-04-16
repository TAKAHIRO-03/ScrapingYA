package jp.co.tk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Appに必要なBeanを生成します。
 */
@Configuration
public class JavaConfig {

    /**
     * 非同期のオブジェクトを返却します。
     *
     * @return Executor
     */
    @Bean("GenImgThread")
    public Executor generateImgTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(3);
        executor.setThreadNamePrefix("GenImgThread-");
        executor.initialize();
        return executor;
    }

    /**
     * 非同期のオブジェクトを返却します。
     *
     * @return Executor
     */
    @Bean("GenCsvThread")
    public Executor generateCsvTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(3);
        executor.setThreadNamePrefix("GenCsvThread-");
        executor.initialize();
        return executor;
    }

}
