package jp.co.tk.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(YAServiceTest.Config.class)
public class YAServiceTest {

    @Autowired
    private YAService serv;

    @ComponentScan("jp.co.tk.domain")
    static class Config {
    }

    @Test
    public void 商品個数を取得することが出来るか() throws Exception {
        var actual = this.serv.count("tomomooo0716");
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void 出品者及び商品情報を取得することが出来るか() throws Exception {
        var actual = this.serv.findSellerBySellerName("tomomooo0716", 5, 0);
        assertThat(actual.getProduct().size()).isEqualTo(5);
    }

    @Test
    public void 画像を生成することが出来るか() throws Exception {
        var seller = this.serv.findSellerBySellerName("tomomooo0716", 5, 0);
        var result = this.serv.generateImg(seller, "./out/tomomooo0716");
        CompletableFuture.completedFuture(result);
    }

}
