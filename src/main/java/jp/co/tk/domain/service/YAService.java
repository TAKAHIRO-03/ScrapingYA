package jp.co.tk.domain.service;

import jp.co.tk.domain.model.YAProduct;
import jp.co.tk.domain.repo.YAReoisitoryImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * ヤフオクから取得したデータを成形等をするサービスクラスです。
 *
 */
@Service
@RequiredArgsConstructor
public class YAService {

    /**
     * Jsoupの戻り値であるDocument型の中身からaタグを抜き出すためのキーです。
     */
    private final static String A_TAG = "a";

    /**
     * Element型からdata-auction-id属性を抜き出すためのキーです。
     */
    private final static String AUCTION_ID = "data-auction-id";

    /**
     * Element型からdata-auction-category属性を抜き出すためのキーです。
     */
    private final static String AUCTION_CATEGORY = "data-auction-category";

    /**
     * ヤフオクにリクエストを送信するクラスです。
     */
    private final YAReoisitoryImpl repo;


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

}
