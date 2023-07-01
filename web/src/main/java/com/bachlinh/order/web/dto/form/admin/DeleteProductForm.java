package com.bachlinh.order.web.dto.form.admin;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DeleteProductForm(@JsonAlias("product_id") String productId) {
}
