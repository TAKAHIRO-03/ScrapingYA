package jp.co.tk.domain.service;

import jp.co.tk.domain.model.Product;
import jp.co.tk.domain.model.Seller;
import jp.co.tk.domain.model.YAProduct;
import jp.co.tk.domain.repo.YARepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * ヤフオクから取得したデータを取得等をするサービスクラスです。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YAService {

    /**
     * ヤフオクからデータを取得するクラスです。
     */
    private final YARepositoryImpl repo;

    /**
     * ヤフオクにリクエストを送る際のスリープ時間を表します。
     */
    private final int sleepMillSecond = 2000;

    /**
     * ヤフオクから１回に何件の情報を取得するかを表します。
     */
    private final int limit = 100;

    /**
     * 画像の拡張子のリストを表します。
     */
    private final static List<String> EXTENSION_LIST = Arrays.asList("jpg", "jpeg", "png");

    /**
     * /の記号を表します。
     */
    private final static String SLASH = "/";

    /**
     * ベースの出力先ディレクトリ
     */
    private final static String BASE_OUT_DIR = "./out/";

    /**
     * ハイフン
     */
    private final static String HYPHEN = "-";


    /**
     * 出品者に紐づく、商品の個数を返却します。
     *
     * @param seller
     * @return 商品数
     * @throws IOException
     */
    public long count(final String seller) throws IOException {
        return this.repo.fetchTotalNumberOfProducts(seller);
    }

    /**
     * 出品者に紐づく、商品を取得します。
     *
     * @param seller
     * @param total
     * @return 出品者情報と商品
     * @throws IOException
     * @throws InterruptedException
     */
    public Seller findSellerBySellerName(final String seller, final long total, final int offsetFromTotal) throws IOException, InterruptedException {

        if (total == 0) {
            return new Seller(seller, Collections.emptySet());
        }

        final var offsetNum = Math.max(Math.ceil((double) total / (double) limit), 1.0);
        final var products = new HashSet<Product>();
        for (int i = 0; i < offsetNum; i++) {
            final Set<YAProduct.IdAndCategory> idAndCategory = this.repo.fetchProductNameListPageBySeller(seller, limit, i * limit + offsetFromTotal);
            Thread.sleep(sleepMillSecond);
            for (final YAProduct.IdAndCategory id : idAndCategory) {
                try {
                    final Product product = this.repo.fetchByProductId(id);
                    products.add(product);
                } catch (final IOException e) {
                    log.error("Catch YAService.findSellerBySellerName. id=".concat(id.toString()), e);
                } finally {
                    Thread.sleep(sleepMillSecond);
                }
            }
        }

        return new Seller(seller, products);
    }

    /**
     * ヤフオクから画像を取得後、画像を生成します。
     *
     * @param seller
     * @throws IOException
     * @throws InterruptedException
     */
    @Async("GenImgThread")
    public CompletableFuture<Void> generateImg(final Seller seller) throws IOException, InterruptedException {

        if (CollectionUtils.isEmpty(seller.getProduct())) {
            log.debug("product is empty. seller=".concat(seller.getName()));
            return CompletableFuture.completedFuture(null);
        }

        for (final var p : seller.getProduct()) {
            final var yap = (YAProduct) p;
            final var imgUrls = new ArrayList<>(yap.getImageUrl());
            final var imgNames = new ArrayList<>(yap.getImageName());
            for (int i = 0, len = imgUrls.size(); i < len; i++) {
                final byte[] imgBinaryData;
                try {
                    imgBinaryData = this.repo.fetchProductImgData(imgUrls.get(i));
                } catch (final IOException e) {
                    log.error("YAService.generateImg.", e);
                    continue;
                } finally {
                    Thread.sleep(sleepMillSecond);
                }
                outputImg(imgBinaryData, imgNames.get(i), BASE_OUT_DIR.concat(seller.getName()));
            }

        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * バイトデータから画像を出力します。
     *
     * @param imgBinaryData
     * @param dirName
     * @throws IOException
     */
    private void outputImg(final byte[] imgBinaryData, final String fileName, final String dirName) throws IOException {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(imgBinaryData)) {
            final var image = ImageIO.read(bis);
            for (final var extension : EXTENSION_LIST) {
                if (fileName.endsWith(extension)) {
                    final var path = dirName.concat(SLASH).concat(fileName);
                    try {
                        ImageIO.write(image, extension, new File(path));
                        log.debug("Generated img. imgPath=".concat(path));
                    } catch (IOException e) {
                        log.error("Catch YAService.generateImg. fileName=".concat(fileName), e);
                    }
                    break;
                }
            }
        }
    }

}
