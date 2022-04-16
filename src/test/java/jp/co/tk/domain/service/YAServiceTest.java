package jp.co.tk.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(YAServiceTest.Config.class)
public class YAServiceTest {

    @Autowired
    private YAService serv;

    @ComponentScan("jp.co.tk.domain")
    static class Config {
    }

}
