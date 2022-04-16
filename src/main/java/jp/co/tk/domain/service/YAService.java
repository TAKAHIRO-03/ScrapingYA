package jp.co.tk.domain.service;

import jp.co.tk.domain.model.YAProduct;
import jp.co.tk.domain.repo.YAReoisitoryImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ヤフオクから取得したデータを成形等をするサービスクラスです。
 */
@Service
@RequiredArgsConstructor
public class YAService {

    /**
     * aタグを抜き出すためのキーです。
     */
    private final static String A_TAG = "a";

    /**
     * spanタグを抜き出すためのキーです。
     */
    private final static String SPAN_TAG = "span";

    /**
     * imgタグを抜き出すためのキーです。
     */
    private final static String IMG_TAG = "img";

    /**
     * src属性を抜き出すためのキーです。
     */
    private final static String SRC_ATTR = "src";

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
     * ヤフオクにリクエストを送信するクラスです。
     */
    private final YAReoisitoryImpl repo;

    /**
     * 正規表現で使用する円です。
     */
    private final static String REGEX_YEN = "円";

    /**
     * 正規表現で使用する数値以外を表す文字です。
     */
    private final static String REGEX_NON_NUM = "\\D";

    /**
     * ブランクを表す文字列です。
     */
    private final static String BLANK = "";


    /**
     * ヤフオクから出品者の商品情報とカテゴリを取得します。
     *
     * @param seller
     * @param limit
     * @param offset
     * @return YAProduct.IdAndCategory IDとカテゴリが保持されたクラス
     * @throws IOException
     */
    public Set<YAProduct.IdAndCategory> findAuctionIdAndCategoryBySeller(final String seller, final int limit, final int offset) throws IOException {
        final var document = repo.fetchProductNameListPageBySeller(seller, limit, offset);
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
     * ヤフオクの商品ページより各種情報を取得します。
     *
     * @param idAndCategory
     * @return YAProduct
     */
    public YAProduct findYAProductByAuctionId(final YAProduct.IdAndCategory idAndCategory) throws IOException {

        final var document = repo.fetchByProductId(idAndCategory.getId());

        final var title = document.getElementsByClass(TITLE_CLASS).text();
        final var description = document.getElementsByClass("ProductExplanation__commentArea").tagName("table").text();
        final var startingPrice = document.getElementsByClass(STARTING_PRICE_CLASS).tagName(SPAN_TAG).eachText().get(9);
        final var buyoutPrice = document.getElementsByClass(BUYNOW_PRICE_CLASS).text();
        final var imgIterator = document.getElementsByClass(IMG_CLASS).tagName(IMG_TAG).iterator();
        final var urls = new HashSet<URL>();
        while (imgIterator.hasNext()) {
            final var p = imgIterator.next();
            final var imgs = p.getElementsByTag(IMG_TAG);
            for (final Element e : imgs) {
                final var urlAsStr = e.absUrl(SRC_ATTR);
                if (StringUtils.isNotBlank(urlAsStr)) {
                    final var url = new URL(urlAsStr);
                    urls.add(url);
                }
            }
        }

        final YAProduct product = YAProduct.builder()
                .idAndCategory(idAndCategory)
                .title(title)
                .description(description)
                .startingPrice(convertToNum(startingPrice))
                .buyoutPrice(convertToNum(startingPrice))
                .imageUrl(urls)
                .build();

        return product;
    }


    /**
     *
     * ヤフオクから取得した価格を数値だけ抜き取ります。
     *
     * @param target
     * @return 数値
     */
    private Long convertToNum(final String target) {
        int index = target.indexOf(REGEX_YEN);
        String startUntilYen = target.substring(0, index);
        return Long.valueOf(startUntilYen.replaceAll(REGEX_NON_NUM, BLANK));
    }

}
