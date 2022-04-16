package jp.co.tk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Appに必要なBeanを生成します。
 */
@Configuration
public class JavaConfig {

    /**
     *
     * HTTP通信するクライアントを定義します。
     *
     * @return RestTemplateオブジェクト
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
