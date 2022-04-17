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

        final var offsetNum = Math.max(Math.ceil(total / limit), 1);
        final var products = new HashSet<Product>();
        for (int i = 0; i < offsetNum; i++) {
            final Set<YAProduct.IdAndCategory> idAndCategory = this.repo.fetchProductNameListPageBySeller(seller, limit, i * limit + offsetFromTotal);
            Thread.sleep(sleepMillSecond);
            for (final YAProduct.IdAndCategory id : idAndCategory) {
                final Product product = this.repo.fetchByProductId(id);
                products.add(product);
                Thread.sleep(sleepMillSecond);
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

        final String dirName = BASE_OUT_DIR.concat(seller.getName());
        for (final var p : seller.getProduct()) {
            final var yap = (YAProduct) p;
            final var imgUrls = yap.getImageUrl();
            for (final var url : imgUrls) {
                final var imgBinaryData = this.repo.fetchProductImgData(url);
                log.debug("url=".concat(url.toString()));
                Thread.sleep(sleepMillSecond);
                try (final ByteArrayInputStream bis = new ByteArrayInputStream(imgBinaryData)) {
                    final var image = ImageIO.read(bis);
                    EXTENSION_LIST.forEach(extension -> {
                        if (url.toString().endsWith(extension)) {
                            final String[] urlSplitedWithSlash = url.toString().split(SLASH);
                            final String fileName = urlSplitedWithSlash[urlSplitedWithSlash.length - 1];
                            final String path = dirName.concat(SLASH).concat(fileName);
                            try {
                                ImageIO.write(image, extension, new File(path));
                            } catch (IOException e) {
                                log.error("Catch YAService.generateImg. url=".concat(url.toString()), e);
                            } finally {
                                log.debug("Generated img. imgPath=".concat(path));
                            }
                        }
                    });
                }
            }

        }

        return CompletableFuture.completedFuture(null);
    }

}
