package jp.co.tk;

import jp.co.tk.domain.model.Seller;
import jp.co.tk.domain.service.CsvService;
import jp.co.tk.domain.service.YAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
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
     * Beanをコピーする処理を提供します。
     */
    private final ModelMapper modelMapper;

    /**
     * ディレクトリ名
     */
    private static final String BASE_DIR;

    /**
     * スラッシュ
     */
    private static final String SLASH = System.getProperty("file.separator");

    /**
     * CSV出力件数
     */
    private static final int LIMIT = 50;

    static {
        BASE_DIR = ".".concat(SLASH).concat("out");
    }

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
            try {
                final var total = this.yaServ.count(trimedSellerAsStr);
                final var offset = Math.max(Math.ceil((double) total / (double) LIMIT), 1.0);
                for (int i = 0; i < offset; i++) {
                    final var seller = this.yaServ.findSellerBySellerName(trimedSellerAsStr, LIMIT, LIMIT * i);
                    final var baseDirWithSellerName = BASE_DIR.concat(SLASH).concat(seller.getName());
                    final var filePath = Paths.get(baseDirWithSellerName.concat(SLASH).concat(String.valueOf(i)));
                    if (!Files.exists(filePath)) {
                        Files.createDirectories(filePath);
                    }
                    this.csvServ.create(seller, filePath.toString());
                    futureResults.add(this.yaServ.generateImg(this.modelMapper.map(seller, Seller.class), filePath.toString()));
                }
            } catch (final IOException | InterruptedException e) {
                log.error("Catch App.run. seller=".concat(trimedSellerAsStr), e);
            }
        }
        final var futureResultsAry = futureResults.toArray(new CompletableFuture[futureResults.size()]);
        CompletableFuture.allOf(futureResultsAry).join();

        log.debug("finish.");
    }

}
