package com.bachlinh.order.web.dto.form.admin.email.template;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public record EmailTemplateDeleteForm(@JsonAlias("template_id") String id) implements ValidatedDto {
}
