package com.bachlinh.order.web.dto.form.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailRemoveForm {

    @JsonAlias("ids")
    private Integer[] cartDetailIds;
}
