package jp.co.tk.domain.repo;

import jp.co.tk.domain.model.Product;
import org.springframework.stereotype.Repository;

/**
 * ヤフオクより
 */
@Repository
public class YAReoisitoryImpl implements WebContentRepository<Product> {

    private final static String SELLER_URL = "https://auctions.yahoo.co.jp/seller";
    //https://auctions.yahoo.co.jp/seller/lxekr88228?sid=lxekr88228&b=1&n=100

    private final static String PRODUCT_URL = "https://page.auctions.yahoo.co.jp";
    //https://page.auctions.yahoo.co.jp/jp/auction/l1047624084

    private final static String OFFSET_KEY = "b";

    private final static String SID_KEY = "sid";

    private final static String LIMIT_KEY = "n";


    /**
     * {@inheritDoc}
     */
    @Override
    public Product fetchByProductId(final String productId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product fetchProductListPageBySeller(final String seller, final int limit, final int offset) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] fetchProductImgData() {
        return new byte[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int fetchTotalNumberOfProducts(final String seller) {
        return 0;
    }

}
