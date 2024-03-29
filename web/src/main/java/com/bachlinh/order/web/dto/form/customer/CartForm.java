package com.bachlinh.order.web.dto.form.customer;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Objects;

public class CartForm implements ValidatedDto {

    @JsonAlias("products")
    private ProductForm[] productForms;

    public ProductForm[] getProductForms() {
        return this.productForms;
    }

    @JsonAlias("products")
    public void setProductForms(ProductForm[] productForms) {
        this.productForms = productForms;
    }

    public record ProductForm(String id, String amount) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProductForm that)) return false;
            return Objects.equals(id, that.id) && Objects.equals(amount, that.amount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, amount);
        }
    }
}
