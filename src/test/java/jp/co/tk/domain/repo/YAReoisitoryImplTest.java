package jp.co.tk.domain.repo;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringJUnitConfig(YAReoisitoryImplTest.Config.class)
public class YAReoisitoryImplTest {

    @Autowired
    private YAReoisitoryImpl repo;

    @ComponentScan("jp.co.tk.domain")
    static class Config {
    }

    @Test
    public void 例外が発生せずにWebページにリクエストが出来ること() throws Exception {
        Thread.sleep(500);
        try {
            this.repo.fetchProductNameListPageBySeller("tomomooo0716", 100, 0);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void 出品者ページのURLが作成出来ること() throws Exception {
        var expected = "https://auctions.yahoo.co.jp/seller/tomomooo0716?sid=tomomooo0716&b=1&n=100";
        var actual = repo.createUrlAsStr("tomomooo0716", 100, 1);
        assertThat(actual).isEqualTo(expected);

        actual = repo.createUrlAsStr("tomomooo0716", 100, 0);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void 出品者ページからデータを取得しIDとカテゴリを取得する() throws Exception {
        Thread.sleep(500);
        var actual = this.repo.fetchProductNameListPageBySeller("tomomooo0716", 100, 0);
        assertThat(actual).hasSize(5);
    }


    @Test
    public void 商品ページから各種情報を取得する() throws Exception {
        Thread.sleep(500);
        var idAndCategorySet = this.repo.fetchProductNameListPageBySeller("tomomooo0716", 100, 0);
        var idAndCategoryList = new ArrayList<>(idAndCategorySet);

        Thread.sleep(500);
        var actual = this.repo.fetchByProductId(idAndCategoryList.get(0));

        assertThat(actual).isNotNull();
    }

}
