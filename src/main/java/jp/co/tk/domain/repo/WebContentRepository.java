package jp.co.tk.domain.repo;

/**
 * Webサイトにリクエストを送信し、HTML等の情報を取得するIFを定義します。
 */
public interface WebContentRepository<T> {

    /**
     * Webコンテンツを取得します。
     *
     * @param productId
     * @return 実装基で定義したクラス
     */
    T fetchByProductId(String productId);

    /**
     * URLを基に、Webコンテンツを取得します。
     * limit offsetを基にデータを取得します。
     *
     * @param seller
     * @param limit
     * @param offset
     * @return 実装基で定義したクラス
     */
    T fetchProductListPageBySeller(String seller, int limit, int offset);


    /**
     * 画像データを取得します。
     *
     * @return バイナリデータ
     */
    byte[] fetchProductImgData();


    /**
     * 商品の合計数を取得します。
     *
     * @param seller 出品者
     * @return 商品数
     */
    int fetchTotalNumberOfProducts(final String seller);

}
