package jp.co.tk.domain.model;

import lombok.Value;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * ヤフオクをスクレイピングした際のデータを保持するクラスです。
 */
public final class YAProduct extends Product {

    /**
     * CSVのヘッダーを表します。
     */
    public static final String CSV_HEADER = "カテゴリ,タイトル,説明,開始価格,即決価格,画像1,画像2,画像3,画像4,画像5,画像6,画像7,画像8,画像9,画像10";

    /**
     * IDとカテゴリ
     */
    private final IdAndCategory idAndCategory;

    /**
     * タイトル
     */
    private final String title;

    /**
     * 説明
     */
    private final String description;

    /**
     * 開始価格
     */
    private final Long startingPrice;

    /**
     * 即決価格
     */
    private final Long buyoutPrice;

    /**
     * 画像URL
     */
    private final Set<URL> imageUrl;

    /**
     * 画像名
     */
    private final Set<String> imageName;

    YAProduct(IdAndCategory idAndCategory, String title, String description, Long startingPrice, Long buyoutPrice, Set<URL> imageUrl, Set<String> imageName) {
        this.idAndCategory = idAndCategory;
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.buyoutPrice = buyoutPrice;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    public static YAProductBuilder builder() {
        return new YAProductBuilder();
    }

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
        sb.append(RegExUtils.replaceAll(title, COMMA, TOHTEN));
        sb.append(COMMA);
        sb.append(RegExUtils.replaceAll(description, COMMA, TOHTEN));
        sb.append(COMMA);
        sb.append(startingPrice);
        sb.append(COMMA);
        sb.append(buyoutPrice);
        sb.append(COMMA);

        final var imgNameLen = imageName.size();
        final var imageNameList = new ArrayList<>(imageName);
        for (int i = 0; i < imgNameLen; i++) {
            sb.append(imageNameList.get(i));
            sb.append(COMMA);
        }
        for (int i = 0, max = 10 - imgNameLen; i < max; i++) {
            sb.append(COMMA);
        }

        final int sbLen = sb.length();
        sb.delete(sbLen - 1, sbLen);

        return sb.toString();
    }

    public IdAndCategory getIdAndCategory() {
        return this.idAndCategory;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public Long getStartingPrice() {
        return this.startingPrice;
    }

    public Long getBuyoutPrice() {
        return this.buyoutPrice;
    }

    public Set<URL> getImageUrl() {
        return this.imageUrl;
    }

    public Set<String> getImageName() {
        return this.imageName;
    }

    public String toString() {
        return "YAProduct(idAndCategory=" + this.getIdAndCategory() + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", startingPrice=" + this.getStartingPrice() + ", buyoutPrice=" + this.getBuyoutPrice() + ", imageUrl=" + this.getImageUrl() + ", imageName=" + this.getImageName() + ")";
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

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof IdAndCategory)) return false;
            final IdAndCategory other = (IdAndCategory) o;
            final Object this$id = this.getId();
            final Object other$id = other.getId();
            if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $id = this.getId();
            result = result * PRIME + ($id == null ? 43 : $id.hashCode());
            return result;
        }

        public String toString() {
            return "YAProduct.IdAndCategory(id=" + this.getId() + ", category=" + this.getCategory() + ")";
        }
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

    public static class YAProductBuilder {
        private IdAndCategory idAndCategory;
        private String title;
        private String description;
        private Long startingPrice;
        private Long buyoutPrice;
        private Set<URL> imageUrl;
        private Set<String> imageName;

        YAProductBuilder() {
        }

        public YAProductBuilder idAndCategory(IdAndCategory idAndCategory) {
            this.idAndCategory = idAndCategory;
            return this;
        }

        public YAProductBuilder title(String title) {
            this.title = title;
            return this;
        }

        public YAProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public YAProductBuilder startingPrice(Long startingPrice) {
            this.startingPrice = startingPrice;
            return this;
        }

        public YAProductBuilder buyoutPrice(Long buyoutPrice) {
            this.buyoutPrice = buyoutPrice;
            return this;
        }

        public YAProductBuilder imageUrl(Set<URL> imageUrl) {
            this.imageUrl = imageUrl;

            final var imgUrlLen = imageUrl.size();
            final var imgUrlList = new ArrayList<>(imageUrl);
            final var imgNameSet = new HashSet<String>();
            for (int i = 0; i < imgUrlLen; i++) {
                final String[] urlSplitedWithSlash = imgUrlList.get(i).toString().split(SLASH);
                final String fileName = urlSplitedWithSlash[urlSplitedWithSlash.length - 1];
                final var hyphenPos = StringUtils.lastIndexOf(fileName, "-");
                final var removedHyphensFileName = StringUtils.substring(fileName, hyphenPos + 1, fileName.length());
                imgNameSet.add(removedHyphensFileName);
            }
            this.imageName = imgNameSet;

            return this;
        }

        public YAProduct build() {
            return new YAProduct(idAndCategory, title, description, startingPrice, buyoutPrice, imageUrl, imageName);
        }

        public String toString() {
            return "YAProduct.YAProductBuilder(idAndCategory=" + this.idAndCategory + ", title=" + this.title + ", description=" + this.description + ", startingPrice=" + this.startingPrice + ", buyoutPrice=" + this.buyoutPrice + ", imageUrl=" + this.imageUrl + ", imageName=" + this.imageName + ")";
        }
    }
}
