package jp.co.tk.domain.repo;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Set;

/**
 * Webサイトにリクエストを送信し、HTML等の情報を取得するIFを定義します。
 */
public interface WebContentRepository<T, U> {

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
    String SLASH = "/";

    /**
     * クエリパラメーターの？を表します。
     */
    String QUERY_PARAM_KEY = "?";

    /**
     * クエリパラメーターのキーと値をつなぐ = を表します。
     */
    String EQUAL = "=";

    /**
     * 複数のクエリパラメーターをつなぐ時の & を表します。
     */
    String AND = "&";

    /**
     * aタグを抜き出すためのキーです。
     */
    String A_TAG = "a";

    /**
     * spanタグを抜き出すためのキーです。
     */
    String SPAN_TAG = "span";

    /**
     * imgタグを抜き出すためのキーです。
     */
    String IMG_TAG = "img";

    /**
     * src属性を抜き出すためのキーです。
     */
    String SRC_ATTR = "src";

    /**
     * ブランクを表す文字列です。
     */
    String BLANK = "";

    /**
     * selectタグを表す文字です。
     */
    String SELECT_TAG = "select";

    /**
     * optionタグを表す文字です。
     */
    String OPTION_TAG = "option";

    /**
     * 半角スペースを表す文字です。
     */
    String SPCAE = " ";

    /**
     * Webコンテンツを取得します。
     *
     * @param productId
     * @return 実装基で定義したクラス
     */
    T fetchByProductId(U productId) throws IOException;

    /**
     * URLを基に、Webコンテンツを取得します。
     * limit offsetを基にデータを取得します。
     *
     * @param seller
     * @param limit
     * @param offset
     * @return 実装基で定義したクラス
     */
    Set<U> fetchProductNameListPageBySeller(String seller, int limit, int offset) throws IOException;


    /**
     * 画像データを取得します。
     *
     * @return バイナリデータ
     */
    byte[] fetchProductImgData(final URL url) throws IOException;


    /**
     * 商品の合計数を取得します。
     *
     * @param seller 出品者
     * @return 商品数
     */
    long fetchTotalNumberOfProducts(final String seller) throws IOException;


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
