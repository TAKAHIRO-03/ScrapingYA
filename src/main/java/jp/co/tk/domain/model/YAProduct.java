package jp.co.tk.domain.model;

import lombok.Builder;
import lombok.Value;

import java.net.URL;
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
    private IdAndCategory idAndCategory;

    /**
     * タイトル
     */
    private String title;

    /**
     * 説明
     */
    private String description;

    /**
     * 開始価格
     */
    private Long startingPrice;

    /**
     * 即決価格
     */
    private Long buyoutPrice;

    /**
     * 画像URL
     */
    private Set<URL> imageUrl;

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
        return null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YAProduct product = (YAProduct) o;
        return Objects.equals(idAndCategory.id, product.idAndCategory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAndCategory.id);
    }

}
