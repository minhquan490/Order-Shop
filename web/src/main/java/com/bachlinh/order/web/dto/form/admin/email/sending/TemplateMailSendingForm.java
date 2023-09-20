package com.bachlinh.order.web.dto.form.admin.email.sending;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class TemplateMailSendingForm implements ValidatedDto {

    @JsonAlias("template_id")
    private String templateId;

    @JsonAlias("to")
    private String toCustomer;

    @JsonAlias("params")
    private TemplateParam[] params;

    public TemplateMailSendingForm() {
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public String getToCustomer() {
        return this.toCustomer;
    }

    public TemplateParam[] getParams() {
        return this.params;
    }

    @JsonAlias("template_id")
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @JsonAlias("to")
    public void setToCustomer(String toCustomer) {
        this.toCustomer = toCustomer;
    }

    @JsonAlias("params")
    public void setParams(TemplateParam[] params) {
        this.params = params;
    }

    public static class TemplateParam {

        @JsonAlias("name")
        private String name;

        @JsonAlias("oldValue")
        private String value;

        public TemplateParam() {
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        @JsonAlias("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonAlias("oldValue")
        public void setValue(String value) {
            this.value = value;
        }
    }
}
