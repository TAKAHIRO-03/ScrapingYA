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
     * スリープ時間をランダムで取得するためのオブジェクトです。
     */
    private final static Random random = new Random();

    /**
     * 画像の拡張子のリストを表します。
     */
    private final static List<String> EXTENSION_LIST = Arrays.asList("jpg", "jpeg", "png");

    /**
     * /の記号を表します。
     */
    private final static String SLASH = System.getProperty("file.separator");

    /**
     * 出品者に紐づく、商品の個数を返却します。
     *
     * @param seller
     * @return 商品数
     * @throws IOException
     */
    public int count(final String seller) throws IOException {
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
    public Seller findSellerBySellerName(final String seller, final int total, final int offset) throws IOException, InterruptedException {

        if (total == 0) {
            return new Seller(seller, Collections.emptySet());
        }

        final var products = new HashSet<Product>();
        final Set<YAProduct.IdAndCategory> idAndCategory = this.repo.fetchProductNameListPageBySeller(seller, total, offset);
        Thread.sleep(getSleepTime());
        for (final YAProduct.IdAndCategory id : idAndCategory) {
            try {
                final Product product = this.repo.fetchByProductId(id);
                products.add(product);
            } catch (final IOException e) {
                log.error("Catch YAService.findSellerBySellerName. id=".concat(id.toString()), e);
            } finally {
                Thread.sleep(getSleepTime());
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
    public CompletableFuture<Void> generateImg(final Seller seller, final String filePath) throws IOException, InterruptedException {

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
                    Thread.sleep(getSleepTime());
                }

                outputImg(imgBinaryData, imgNames.get(i), filePath);
            }

        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * バイトデータから画像を出力します。
     *
     * @param imgBinaryData
     * @param filePath
     * @throws IOException
     */
    private void outputImg(final byte[] imgBinaryData, final String fileName, final String filePath) throws IOException {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(imgBinaryData)) {
            final var image = ImageIO.read(bis);
            for (final var extension : EXTENSION_LIST) {
                if (fileName.endsWith(extension)) {
                    final var path = filePath.concat(SLASH).concat(fileName);
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


    /**
     * 2000~3000の間の数値を取得します。
     *
     * @return 2000~3000
     */
    private long getSleepTime() {
        return random.nextInt(1001) + 2000;
    }

}
