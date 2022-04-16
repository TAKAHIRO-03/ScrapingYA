package jp.co.tk.domain;

import jp.co.tk.domain.model.Product;
import jp.co.tk.domain.model.Seller;

import java.util.Set;

/**
 * SellerのCSVファイルを生成します。
 */
public class CsvCreateSeller extends Seller implements CsvCreator {

    /**
     * コンストラクタを定義しています。
     *
     * @param name    出品者名
     * @param product 商品
     */
    public CsvCreateSeller(final String name, final Set<Product> product) {
        super(name, product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create() {
    }

}
