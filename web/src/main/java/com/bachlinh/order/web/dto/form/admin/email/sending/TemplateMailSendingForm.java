package com.bachlinh.order.web.dto.form.admin.email.sending;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class TemplateMailSendingForm implements ValidatedDto {

    @JsonAlias("template_id")
    private String templateId;

    @JsonAlias("to")
    private String toCustomer;

    @JsonAlias("params")
    private TemplateParam[] params;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TemplateParam {

        @JsonAlias("name")
        private String name;

        @JsonAlias("value")
        private String value;
    }
}
