package com.bachlinh.order.web.dto.form.admin;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CustomerDeleteForm(@JsonAlias("customer_id") String customerId) {
}
