package jp.co.tk;

import jp.co.tk.domain.service.CsvCreatorService;
import jp.co.tk.domain.service.YAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * Appの呼び出し基クラスです。
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class App implements ApplicationRunner {

    /**
     * エントリーポイント
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * 文字コード
     */
    private static final String CSV_CHARSET = "Shift-JIS";

    /**
     * 出品者のファイルパス
     */
    private final static String SELLER_PATH = "./in/seller.csv";

    /**
     * カンマを表すフィールドです。
     */
    private final static String COMMA = ",";

    /**
     * ヤフオフからデータを取得するなどの処理を提供します。
     */
    private final YAService yaServ;

    /**
     * CSVファイルを生成する処理を提供します。
     */
    private final CsvCreatorService csvCreatorServ;

    /**
     * 出品者の情報を1回で取得することが出来る商品数
     */
    private final int MAX_FETCH_DATA = 500;

    /**
     * Appが実行されたとき最初に呼ばれる関数です。
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        final Path file = Paths.get(SELLER_PATH);
        final String sellerAsStrAry[] = Files.readString(file, Charset.forName(CSV_CHARSET)).split(COMMA);

        CompletableFuture<Void> imgFuture = null;
        CompletableFuture<Void> csvFuture = null;
        for (final var sellerAsStr : sellerAsStrAry) {

            final var trimedSellerAsStr = sellerAsStr.trim();
            final var total = this.yaServ.count(trimedSellerAsStr);
            if (500 < total) {
                final var offset = Math.ceil(total / MAX_FETCH_DATA);
                for (int i = 0; i < offset; i++) {
                    var seller = this.yaServ.findSellerBySellerName(trimedSellerAsStr, MAX_FETCH_DATA, MAX_FETCH_DATA);
                    csvFuture = this.csvCreatorServ.create(seller);
                    imgFuture = this.yaServ.generateImg(seller);
                }
                continue;
            }

            var seller = this.yaServ.findSellerBySellerName(trimedSellerAsStr, total, 0);
            csvFuture = this.csvCreatorServ.create(seller);
            imgFuture = this.yaServ.generateImg(seller);
        }
        CompletableFuture.allOf(imgFuture, csvFuture);

        log.debug("finish.");
    }

}
