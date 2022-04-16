package jp.co.tk.domain.repo;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;

/**
 * Webサイトにリクエストを送信し、HTML等の情報を取得するIFを定義します。
 */
public interface WebContentRepository<T> {

    /**
     * Webページにリクエストする際のユーザーエージェントを定義します。
     */
    String USER_AGENTS[] = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/605.1.15 (KHTML, like Gecko)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15",
            "Opera/9.80 (Windows NT 6.1; WOW64) Presto/2.12.388 Version/12.18",
    };

    /**
     * 以下、クエリパラーメーターを作成する際にしようする文字列です。
     */
    public final static String SLASH = "/";

    /**
     * クエリパラメーターの？を表します。
     */
    public final static String QUERY_PARAM_KEY = "?";

    /**
     * クエリパラメーターのキーと値をつなぐ = を表します。
     */
    public final static String EQUAL = "=";

    /**
     * 複数のクエリパラメーターをつなぐ時の & を表します。
     */
    public final static String AND = "&";

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
    Document fetchProductNameListPageBySeller(String seller, int limit, int offset) throws IOException;


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


    /**
     * ユーザーエージェントを取得します。
     *
     * @return ユーザーエージェント
     */
    default String getRandomUserAgent() {
        final var random = new Random();
        final var randomValue = random.nextInt(4);
        return USER_AGENTS[randomValue];
    }

}
