package jp.co.tk;

import jp.co.tk.domain.service.CsvService;
import jp.co.tk.domain.service.YAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
     * ヤフオフからデータを取得するなどの処理を提供します。
     */
    private final YAService yaServ;

    /**
     * CSVファイルを生成する処理を提供します。
     */
    private final CsvService csvServ;

    /**
     * ディレクトリ名
     */
    private static final String BASE_DIR = "./out";

    /**
     * スラッシュ
     */
    private static final String SLASH = "/";

    /**
     * Appが実行されたとき最初に呼ばれる関数です。
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        final var sellerAsStrAry = this.csvServ.readSellerList();

        final var futureResults = new ArrayList<CompletableFuture<Void>>();
        for (final var sellerAsStr : sellerAsStrAry) {

            final var trimedSellerAsStr = sellerAsStr.trim();
            final var total = this.yaServ.count(trimedSellerAsStr);

            try {
                var seller = this.yaServ.findSellerBySellerName(trimedSellerAsStr, total, 0);
                final String baseDirWithSellerName = BASE_DIR.concat(SLASH).concat(seller.getName());
                final Path filePath = Paths.get(baseDirWithSellerName);
                if (!Files.exists(filePath)) {
                    Files.createDirectory(filePath);
                }
                futureResults.add(this.csvServ.create(seller));
                futureResults.add(this.yaServ.generateImg(seller));
            } catch (final IOException | InterruptedException e) {
                log.error("Catch App.run. seller=".concat(trimedSellerAsStr));
            }
        }
        final var futureResultsAry = futureResults.toArray(new CompletableFuture[futureResults.size()]);
        CompletableFuture.allOf(futureResultsAry).join();

        log.debug("finish.");
    }

}
