package com.bachlinh.order.web.dto.form.admin.product;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

@ActiveReflection
public class ProductCreateForm implements ValidatedDto {

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

    public String getProductName() {
        return this.productName;
    }

    public String getProductPrice() {
        return this.productPrice;
    }

    public String getProductSize() {
        return this.productSize;
    }

    public String getProductColor() {
        return this.productColor;
    }

    public String getProductTaobaoUrl() {
        return this.productTaobaoUrl;
    }

    public String getProductDescription() {
        return this.productDescription;
    }

    public String getProductEnabled() {
        return this.productEnabled;
    }

    public String[] getProductCategoriesId() {
        return this.productCategoriesId;
    }

    public String getProductOrderPoint() {
        return this.productOrderPoint;
    }
}
