package com.bachlinh.order.web.dto.form.admin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.RequiredArgsConstructor;
import com.bachlinh.order.validate.base.ValidatedDto;

@RequiredArgsConstructor
public record EmailTemplateFolderCreateForm(@JsonAlias("name") String name,
                                            @JsonAlias("clean_policy") Integer cleanPolicy) implements ValidatedDto {
}
