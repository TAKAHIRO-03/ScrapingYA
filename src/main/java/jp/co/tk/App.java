package jp.co.tk;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Appの呼び出し基クラスです。
 */
@SpringBootApplication
public class App implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * Appが呼ばれたとき最初に呼ばれる関数です。
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        //TODO ファイルから出品者を取得するための処理を呼ぶ

        //TODO 出品者情報を基に、for文で順に処理を呼んでいく。

    }

}
