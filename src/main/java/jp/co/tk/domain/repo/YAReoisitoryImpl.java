package jp.co.tk.domain.repo;

import jp.co.tk.domain.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * ヤフオクより商品を取得します。
 */
@Slf4j
@Repository
public class YAReoisitoryImpl implements WebContentRepository<Product> {

    /**
     * 出品者のベースURLを表します。
     */
    private final static String SELLER_URL = "https://auctions.yahoo.co.jp/seller";

    /**
     * 商品のベースURLを表します。
     */
    private final static String PRODUCT_URL = "https://page.auctions.yahoo.co.jp";

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
    public Product fetchByProductId(final String productId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document fetchProductNameListPageBySeller(final String seller, final int limit, final int offset) throws IOException {

        final var userAgent = getRandomUserAgent();
        final var url = createUrlAsStr(seller, limit, offset);
        final var document = Jsoup.connect(url).userAgent(userAgent).timeout(60000).get();

        final var documentAsStr = document.getElementsByTag("a").stream()
                .map(x -> x.getElementsByAttribute("data-auction-id").attr("data-auction-id"))
                .filter(x -> StringUtils.isNotBlank(x))
                .collect(Collectors.toSet());

        return document;
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

}
