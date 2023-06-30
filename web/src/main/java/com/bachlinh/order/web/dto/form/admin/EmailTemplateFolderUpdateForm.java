package com.bachlinh.order.web.dto.form.admin;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.bachlinh.order.validate.base.ValidatedDto;

public record EmailTemplateFolderUpdateForm(@JsonAlias("id") String id,
                                            @JsonAlias("name") String name,
                                            @JsonAlias("clean_policy") Integer cleanPolicy) implements ValidatedDto {
}
