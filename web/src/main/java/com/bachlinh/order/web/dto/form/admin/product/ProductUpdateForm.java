package com.bachlinh.order.web.dto.form.admin.product;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;

@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Getter
public class ProductUpdateForm implements ValidatedDto {

    @JsonAlias("product_id")
    private String productId;

    @JsonAlias("product_name")
    private String productName;

    @JsonAlias("product_price")
    private String productPrice;

    @JsonAlias("product_size")
    private String productSize;

    @JsonAlias("product_color")
    private String productColor;

    @JsonAlias("product_taobao_url")
    private String productTaobaoUrl;

    @JsonAlias("product_description")
    private String productDescription;

    @JsonAlias("product_enabled")
    private String productEnabled;

    @JsonAlias("product_categories")
    private String[] productCategoriesId;

    @JsonAlias("product_order_point")
    private String productOrderPoint;

    @ActiveReflection
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @ActiveReflection
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @ActiveReflection
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    @ActiveReflection
    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    @ActiveReflection
    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    @ActiveReflection
    public void setProductTaobaoUrl(String productTaobaoUrl) {
        this.productTaobaoUrl = productTaobaoUrl;
    }

    @ActiveReflection
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @ActiveReflection
    public void setProductEnabled(String productEnabled) {
        this.productEnabled = productEnabled;
    }

    @ActiveReflection
    public void setProductCategoriesId(String[] productCategoriesId) {
        this.productCategoriesId = productCategoriesId;
    }

    @ActiveReflection
    public void setProductOrderPoint(String productOrderPoint) {
        this.productOrderPoint = productOrderPoint;
    }
}
