package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Collection;

@JsonRootName("order")
public record OrderProductForm(@JsonAlias("details") Collection<OrderDetailForm> details) {

    @JsonRootName("detail")
    public record OrderDetailForm(@JsonAlias("product_id") String productId, @JsonAlias("amount") int amount) {

    }
}
