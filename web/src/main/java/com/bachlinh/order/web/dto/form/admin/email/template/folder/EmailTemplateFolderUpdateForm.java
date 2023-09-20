package com.bachlinh.order.web.dto.form.admin.email.template.folder;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public record EmailTemplateFolderUpdateForm(@JsonAlias("id") String id,
                                            @JsonAlias("name") String name,
                                            @JsonAlias("clean_policy") Integer cleanPolicy) implements ValidatedDto {
}
