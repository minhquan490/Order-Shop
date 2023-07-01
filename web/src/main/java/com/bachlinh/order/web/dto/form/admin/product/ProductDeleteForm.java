package com.bachlinh.order.web.dto.form.admin.product;

import com.fasterxml.jackson.annotation.JsonAlias;

public record ProductDeleteForm(@JsonAlias("product_id") String productId) {
}
