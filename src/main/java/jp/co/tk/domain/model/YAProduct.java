package jp.co.tk.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.net.URL;
import java.util.List;

/**
 * ヤフオクをスクレイピングした際のデータを保持するクラスです。
 */
@Builder
@Value
@EqualsAndHashCode(of = "id")
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
    List<URL> imageUrl;

    /**
     * 画像バイナリーデータ
     */
    List<Byte> image;

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

}
