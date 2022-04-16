package jp.co.tk.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringJUnitConfig(YAServiceTest.Config.class)
public class YAServiceTest {

    @Autowired
    private YAService serv;

    @ComponentScan("jp.co.tk.domain")
    static class Config {
    }

    @Test
    public void 出品者ページからデータを取得しIDとカテゴリを取得する() throws Exception {
        var actual = this.serv.findAuctionIdAndCategoryBySeller("tomomooo0716", 100, 0);
        assertThat(actual).hasSize(5);
    }

}
