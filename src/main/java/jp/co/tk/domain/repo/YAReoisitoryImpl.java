package jp.co.tk.domain.repo;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * ヤフオクより商品を取得します。
 */
@Slf4j
@Repository
public class YAReoisitoryImpl implements WebContentRepository {

    /**
     * 出品者のベースURLを表します。
     */
    private final static String SELLER_URL = "https://auctions.yahoo.co.jp/seller";

    /**
     * 商品のベースURLを表します。
     */
    private final static String PRODUCT_URL = "https://page.auctions.yahoo.co.jp/jp/auction";

    /**
     * 出品者ページのクエリパラメーターのオフセットのキーを表します。
     */
    private final static String OFFSET_KEY = "b";

    /**
     * 出品者ページのクエリパラメーターの出品者のキーを表します。
     */
    private final static String SID_KEY = "sid";

    /**
     * 出品者ページのクエリパラメーターのリミットのキーを表します。
     */
    private final static String LIMIT_KEY = "n";


    /**
     * {@inheritDoc}
     */
    @Override
    public Document fetchByProductId(final String productId) throws IOException {
        final var userAgent = getRandomUserAgent();
        final var url = createUrlAsStr(productId);
        return Jsoup.connect(url).userAgent(userAgent).timeout(60000).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document fetchProductNameListPageBySeller(final String seller, final int limit, final int offset) throws IOException {
        final var userAgent = getRandomUserAgent();
        final var url = createUrlAsStr(seller, limit, offset);
        return Jsoup.connect(url).userAgent(userAgent).timeout(60000).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fetchProductImgData() {
        return new byte[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int fetchTotalNumberOfProducts(final String seller) {
        return 0;
    }


    /**
     * 出品者ページのURLを作成します。
     *
     * @param seller 出品者
     * @param limit  最大値
     * @param offset オフセット値
     * @return URL
     */
    String createUrlAsStr(final String seller, final int limit, final int offset) {

        final var sb = new StringBuilder();
        sb.append(SELLER_URL);
        sb.append(SLASH);
        sb.append(seller);
        sb.append(QUERY_PARAM_KEY);
        sb.append(SID_KEY);
        sb.append(EQUAL);
        sb.append(seller);
        sb.append(AND);
        sb.append(OFFSET_KEY);
        sb.append(EQUAL);
        sb.append(Math.max(offset, 1));
        sb.append(AND);
        sb.append(LIMIT_KEY);
        sb.append(EQUAL);
        sb.append(limit);

        final var url = sb.toString();
        log.debug("url=".concat(url));

        return url;
    }


    /**
     * 商品ページのURLを作成します。
     *
     * @param auctionId 出品者
     * @return URL
     */
    String createUrlAsStr(final String auctionId) {

        final var sb = new StringBuilder();
        sb.append(PRODUCT_URL);
        sb.append(SLASH);
        sb.append(auctionId);
        final var url = sb.toString();
        log.debug("url=".concat(url));

        return url;
    }

}
