package com.bachlinh.order.web.dto.form.admin.email.template.folder;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public record EmailTemplateFolderCreateForm(@JsonAlias("name") String name,
                                            @JsonAlias("clean_policy") Integer cleanPolicy) implements ValidatedDto {
}
