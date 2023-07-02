package com.bachlinh.order.web.dto.form.admin.email.template;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.bachlinh.order.validate.base.ValidatedDto;

public record EmailTemplateDeleteForm(@JsonAlias("template_id") String id) implements ValidatedDto {
}
