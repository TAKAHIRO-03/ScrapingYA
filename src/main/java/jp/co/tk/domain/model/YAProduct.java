package jp.co.tk.domain.model;

import lombok.Builder;
import lombok.Value;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * ヤフオクをスクレイピングした際のデータを保持するクラスです。
 */
@Builder
@Value
public class YAProduct extends Product {

    /**
     * CSVのヘッダーを表します。
     */
    public static final String CSV_HEADER = "カテゴリ,タイトル,説明,開始価格,即決価格,画像1,画像2,画像3,画像4,画像5,画像6,画像7,画像8,画像9,画像10";

    /**
     * IDとカテゴリ
     */
    IdAndCategory idAndCategory;

    /**
     * タイトル
     */
    String title;

    /**
     * 説明
     */
    String description;

    /**
     * 開始価格
     */
    Long startingPrice;

    /**
     * 即決価格
     */
    Long buyoutPrice;

    /**
     * 画像URL
     */
    Set<URL> imageUrl;

    /**
     * {@inheritDoc}
     */
    @Override
    public String csvHeader() {
        return CSV_HEADER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String csvData() {

        final var sb = new StringBuilder();
        sb.append(idAndCategory.getCategory());
        sb.append(COMMA);
        sb.append(title);
        sb.append(COMMA);
        sb.append(description);
        sb.append(COMMA);
        sb.append(startingPrice);
        sb.append(COMMA);
        sb.append(buyoutPrice);
        sb.append(COMMA);

        final var imgUrlLen = imageUrl.size();
        final var imgUrlList = new ArrayList<>(imageUrl);
        for (int i = 0; i < imgUrlLen; i++) {
            final String[] urlSplitedWithSlash = imgUrlList.get(i).toString().split(SLASH);
            final String fileName = urlSplitedWithSlash[urlSplitedWithSlash.length - 1];
            sb.append(fileName);
            sb.append(COMMA);
        }
        for (int i = 0, max = 10 - imgUrlLen; i < max; i++) {
            sb.append(COMMA);
        }

        final int sbLen = sb.length();
        sb.delete(sbLen - 2, sbLen);

        return sb.toString();
    }

    /**
     * IDとカテゴリを保持するインナークラスです。
     */
    @Value
    public static class IdAndCategory {

        /**
         * プロダクトID
         */
        String id;

        /**
         * カテゴリ
         */
        String category;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YAProduct product = (YAProduct) o;
        return Objects.equals(idAndCategory.id, product.idAndCategory.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(idAndCategory.id);
    }

}
