package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class EmailSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;
}
