package com.bachlinh.order.web.dto.form.admin.order;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;

public record OrderProductForm(@JsonAlias("details") Collection<OrderDetailForm> details,
                               @JsonAlias("id") String orderId,
                               @JsonAlias("status") String status,
                               @JsonIgnore String customerId) {

    public record OrderDetailForm(@JsonAlias("product_id") String productId, @JsonAlias("amount") int amount) {

    }
}
