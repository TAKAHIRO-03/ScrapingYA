package jp.co.tk.domain.model;

/**
 * 商品の基底クラスを定義します。
 */
public abstract class Product {

    /**
     * カンマを表すフィールドです。
     */
    public static String COMMA = ",";

    /**
     * スラッシュを表すフィールドです。
     */
    public static String SLASH = "/";

    /**
     * スラッシュを表すフィールドです。
     */
    public static String TOHTEN = "、";

    /**
     * CSVのヘッダー情報を取得します。
     *
     * @return CSVヘッダー
     */
    public abstract String csvHeader();

    /**
     * 1行単位のCSVデータを取得します。
     *
     * @return CSVデータ
     */
    public abstract String csvData();

}
