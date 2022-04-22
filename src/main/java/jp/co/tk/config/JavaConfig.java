package jp.co.tk.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Appに必要なBeanを生成します。
 */
@EnableAsync
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
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("GenImgThread-");
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
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("GenCsvThread-");
        return executor;
    }

    /**
     * Beanをコピーオブジェクトを返却します。
     *
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
