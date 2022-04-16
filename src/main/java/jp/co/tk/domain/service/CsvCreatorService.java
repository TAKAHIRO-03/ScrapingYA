package jp.co.tk.domain.service;

import jp.co.tk.domain.model.Seller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * CSVファイルするサービスクラスです。
 */
@Slf4j
@Service
public class CsvCreatorService {

    /**
     * 文字コード
     */
    private static final String CSV_CHARSET = "Shift-JIS";

    /**
     * ディレクトリ名
     */
    private static final String BASE_DIR = "./out";

    /**
     * スラッシュ
     */
    private static final String SLASH = "/";

    /**
     * ハイフン
     */
    private static final String HYPHEN = "-";

    /**
     * csvの拡張子
     */
    private static final String EXSTENSION = ".csv";

    /**
     * 改行コード
     */
    private static final String LF = "\r\n";

    /**
     * 出品者情報を基にCSVファイルを生成します。
     *
     * @param seller
     * @throws IOException
     */
    @Async("GenCsvThread")
    public CompletableFuture<Void> create(final Seller seller) throws IOException {

        final var fileBlr = new StringBuilder(BASE_DIR);
        fileBlr.append(SLASH);
        fileBlr.append(seller.getName());
        fileBlr.append(SLASH);
        fileBlr.append(seller.getName());
        fileBlr.append(HYPHEN);
        fileBlr.append(System.currentTimeMillis());
        fileBlr.append(EXSTENSION);

        final String baseDirWithSellerName = BASE_DIR.concat(SLASH).concat(seller.getName());
        final Path filePath = Paths.get(baseDirWithSellerName);
        if (!Files.exists(filePath)) {
            Files.createDirectory(filePath);
        }

        try (final var fw = new FileWriter(fileBlr.toString(), Charset.forName(CSV_CHARSET))) {
            var isFirst = true;
            for (final var p : seller.getProduct()) {
                if (isFirst) {
                    fw.write(p.csvHeader());
                    fw.write(LF);
                    isFirst = false;
                }
                fw.write(p.csvData());
                fw.write(LF);
            }
        }
        log.debug("generate csv. path=".concat(fileBlr.toString()));

        return CompletableFuture.completedFuture(null);
    }

}
