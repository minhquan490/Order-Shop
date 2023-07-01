package com.bachlinh.order.web.dto.form.admin.email.template.folder;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.bachlinh.order.validate.base.ValidatedDto;

public record EmailTemplateFolderCreateForm(@JsonAlias("name") String name,
                                            @JsonAlias("clean_policy") Integer cleanPolicy) implements ValidatedDto {
}
