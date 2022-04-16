package jp.co.tk.domain.repo;

import jp.co.tk.domain.model.Product;
import jp.co.tk.domain.model.YAProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ヤフオクより商品を取得します。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class YAReoisitoryImpl implements WebContentRepository<Product, YAProduct.IdAndCategory> {

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
     * data-auction-id属性を抜き出すためのキーです。
     */
    private final static String AUCTION_ID = "data-auction-id";

    /**
     * data-auction-category属性を抜き出すためのキーです。
     */
    private final static String AUCTION_CATEGORY = "data-auction-category";

    /**
     * タイトルを抜き出すためのHTMLクラス名です。
     */
    private final static String TITLE_CLASS = "ProductTitle__text";

    /**
     * 説明を抜き出すためのHTMLクラス名です。
     */
    private final static String STARTING_PRICE_CLASS = "ProductDetail__description";

    /**
     * 開始価格を抜き出すためのHTMLクラス名です。
     */
    private final static String BUYNOW_PRICE_CLASS = "Price--buynow";

    /**
     * イメージURLを抜き出すためのHTMLクラス名です。
     */
    private final static String IMG_CLASS = "ProductImage__image";

    /**
     * 合計値を抜き出すためのHTMLクラス名です。
     */
    private final static String PU_CLASS = "pu";

    /**
     * 正規表現で使用する円です。
     */
    private final static String REGEX_YEN = "円";

    /**
     * 正規表現で使用する数値以外を表す文字です。
     */
    private final static String REGEX_NON_NUM = "\\D";

    /**
     * @param idAndCategory
     * @return
     * @throws IOException
     */
    @Override
    public Product fetchByProductId(final YAProduct.IdAndCategory idAndCategory) throws IOException {
        final var userAgent = getRandomUserAgent();
        final var url = createUrlAsStr(idAndCategory.getId());
        final var document = Jsoup.connect(url).userAgent(userAgent).timeout(60000).get();

        final var title = document.getElementsByClass(TITLE_CLASS).text();
        final var description = document.getElementsByClass("ProductExplanation__commentArea").tagName("table").text();
        final var startingPrice = document.getElementsByClass(STARTING_PRICE_CLASS).tagName(SPAN_TAG).eachText().get(9);
        final var buyoutPrice = document.getElementsByClass(BUYNOW_PRICE_CLASS).text();
        final var imgIterator = document.getElementsByClass(IMG_CLASS).tagName(IMG_TAG).iterator();
        final var imgUrls = new HashSet<URL>();
        while (imgIterator.hasNext()) {
            final var p = imgIterator.next();
            final var imgs = p.getElementsByTag(IMG_TAG);
            for (final Element e : imgs) {
                final var urlAsStr = e.absUrl(SRC_ATTR);
                if (StringUtils.isNotBlank(urlAsStr)) {
                    final var imgUrl = new URL(urlAsStr);
                    imgUrls.add(imgUrl);
                }
            }
        }

        final YAProduct product = YAProduct.builder()
                .idAndCategory(idAndCategory)
                .title(title)
                .description(description)
                .startingPrice(convertToNum(startingPrice))
                .buyoutPrice(convertToNum(buyoutPrice))
                .imageUrl(imgUrls)
                .build();

        return product;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<YAProduct.IdAndCategory> fetchProductNameListPageBySeller(final String seller, final int limit, final int offset) throws IOException {
        final var userAgent = getRandomUserAgent();
        final var url = createUrlAsStr(seller, limit, offset);
        final var document = Jsoup.connect(url).userAgent(userAgent).timeout(60000).get();
        final var idAndCategorySet = document.getElementsByTag(A_TAG).stream()
                .map(x -> {
                    final String id = x.getElementsByAttribute(AUCTION_ID).attr(AUCTION_ID);
                    final String category = x.getElementsByAttribute(AUCTION_ID).attr(AUCTION_CATEGORY);
                    if (StringUtils.isBlank(id) || StringUtils.isBlank(category)) {
                        return null;
                    }
                    return new YAProduct.IdAndCategory(id, category);
                })
                .filter(x -> Objects.nonNull(x))
                .collect(Collectors.toSet());

        return idAndCategorySet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fetchProductImgData(final URL url) throws IOException {
        final var userAgent = getRandomUserAgent();
        return Jsoup.connect(url.toString()).userAgent(userAgent).timeout(60000).ignoreContentType(true).execute().bodyAsBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int fetchTotalNumberOfProducts(final String seller) throws IOException {

        final var userAgent = getRandomUserAgent();
        final var urlBlr = new StringBuilder(SELLER_URL);
        urlBlr.append(SLASH);
        urlBlr.append(seller);
        log.debug(urlBlr.toString());
        final var document = Jsoup.connect(urlBlr.toString()).userAgent(userAgent).timeout(60000).get();
        final var elementsWithTotal = document.getElementsByClass(PU_CLASS).tagName(SELECT_TAG).tagName(OPTION_TAG).eachText();
        final int total;
        if (!CollectionUtils.isEmpty(elementsWithTotal)) {
            final String[] splitedSpaceAry = elementsWithTotal.get(0).split(SPCAE);
            total = Integer.valueOf(splitedSpaceAry[0].replaceAll(REGEX_NON_NUM, BLANK));
        } else {
            total = 0;
        }

        return total;
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

    /**
     * ヤフオクから取得した価格を数値だけ抜き取ります。
     *
     * @param target
     * @return 数値
     */
    Long convertToNum(final String target) {
        int index = target.indexOf(REGEX_YEN);
        String startUntilYen = target.substring(0, index);
        return Long.valueOf(startUntilYen.replaceAll(REGEX_NON_NUM, BLANK));
    }

}
