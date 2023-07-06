package com.bachlinh.order.web.dto.form.admin.email.template;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class EmailTemplateSearchForm implements ValidatedDto {

    @JsonAlias("query")
    private String query;
}
