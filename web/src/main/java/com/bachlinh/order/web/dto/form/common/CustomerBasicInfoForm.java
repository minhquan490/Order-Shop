package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerBasicInfoForm {

    @JsonAlias("token")
    private String token;
}
