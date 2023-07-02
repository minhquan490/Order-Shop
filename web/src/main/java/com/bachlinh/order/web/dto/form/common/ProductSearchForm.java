package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Arrays;
import java.util.Objects;

public record ProductSearchForm(@JsonAlias("name") String productName,
                                @JsonAlias("price") String price,
                                @JsonAlias("size") String productSize,
                                @JsonAlias("color") String color,
                                @JsonAlias("enable") String enable,
                                @JsonAlias("categories") String[] categories,
                                @JsonAlias("page") String page,
                                @JsonAlias("page_size") String size,
                                @JsonAlias("mode") String mode) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductSearchForm that)) return false;
        return Objects.equals(productName, that.productName) && Objects.equals(price, that.price) && Objects.equals(productSize, that.productSize) && Objects.equals(color, that.color) && Objects.equals(enable, that.enable) && Arrays.equals(categories, that.categories) && Objects.equals(page, that.page) && Objects.equals(size, that.size) && Objects.equals(mode, that.mode);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(productName, price, productSize, color, enable, page, size, mode);
        result = 31 * result + Arrays.hashCode(categories);
        return result;
    }

    @Override
    public String toString() {
        return "ProductSearchForm{" +
                "productName='" + productName + '\'' +
                ", price='" + price + '\'' +
                ", productSize='" + productSize + '\'' +
                ", color='" + color + '\'' +
                ", enable='" + enable + '\'' +
                ", categories=" + Arrays.toString(categories) +
                ", page='" + page + '\'' +
                ", size='" + size + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
