package jp.co.tk.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;


/**
 * 出品者とN個の商品を保持するデータクラスです。
 */
@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class Seller {

    /**
     * 出品者名を表すフィールドです。
     */
    private final String name;

    /**
     * 商品を表すフィールドです。
     */
    private final Set<Product> product;

}
