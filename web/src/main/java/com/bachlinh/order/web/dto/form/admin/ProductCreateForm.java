package com.bachlinh.order.web.dto.form.admin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;

@ActiveReflection
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
@Getter
@Setter(onMethod_ = @ActiveReflection)
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
}
