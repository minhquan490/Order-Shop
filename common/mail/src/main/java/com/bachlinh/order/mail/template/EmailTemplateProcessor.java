package com.bachlinh.order.mail.template;

public interface EmailTemplateProcessor {
    String process(String template, BindingModel model);

    static EmailTemplateProcessor defaultInstance() {
        return new EmailTemplateProcessorImpl();
    }
}
