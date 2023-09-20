package com.bachlinh.order.web.dto.form.customer;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CartDetailRemoveForm {

    @JsonAlias("ids")
    private Integer[] cartDetailIds;

    public Integer[] getCartDetailIds() {
        return this.cartDetailIds;
    }

    @JsonAlias("ids")
    public void setCartDetailIds(Integer[] cartDetailIds) {
        this.cartDetailIds = cartDetailIds;
    }
}
