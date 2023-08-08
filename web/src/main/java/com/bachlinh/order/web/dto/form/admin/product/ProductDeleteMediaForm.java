package com.bachlinh.order.web.dto.form.admin.product;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDeleteMediaForm {

    @JsonAlias("media_url")
    private String mediaUrl;
}
