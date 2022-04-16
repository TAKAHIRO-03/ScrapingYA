package jp.co.tk.domain.repo;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.net.URL;
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

    @Test
    public void 商品の合計数を取得する() throws Exception {
        Thread.sleep(500);
        var actual = this.repo.fetchTotalNumberOfProducts("tomomooo0716");
        assertThat(actual).isEqualTo(5);
    }

    @Test
    public void 画像データをバイナリで取得する() throws Exception {
        final String urlAsStr = "https://auctions.c.yimg.jp/images.auctions.yahoo.co.jp/image/dr000/auc0304/users/db78dcdb3b6b11c522fb507729f2f1b712ce01df/i-img600x600-16500834183vqiii49430.jpg";
        var actual = this.repo.fetchProductImgData(new URL(urlAsStr));

        assertThat(actual).isNotNull();
    }

}
