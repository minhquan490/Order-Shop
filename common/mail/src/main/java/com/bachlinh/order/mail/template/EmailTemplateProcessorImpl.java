package com.bachlinh.order.mail.template;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.IThrottledTemplateProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.IContext;

import java.io.Writer;
import java.util.Set;

class EmailTemplateProcessorImpl implements ITemplateEngine, EmailTemplateProcessor {
    private final TemplateEngine delegateEngine = new TemplateEngine();

    @Override
    public IEngineConfiguration getConfiguration() {
        return delegateEngine.getConfiguration();
    }

    @Override
    public String process(String template, IContext context) {
        return delegateEngine.process(template, context);
    }

    @Override
    public String process(String template, Set<String> templateSelectors, IContext context) {
        return delegateEngine.process(template, templateSelectors, context);
    }

    @Override
    public String process(TemplateSpec templateSpec, IContext context) {
        return delegateEngine.process(templateSpec, context);
    }

    @Override
    public void process(String template, IContext context, Writer writer) {
        delegateEngine.process(template, context, writer);
    }

    @Override
    public void process(String template, Set<String> templateSelectors, IContext context, Writer writer) {
        delegateEngine.process(template, templateSelectors, context, writer);
    }

    @Override
    public void process(TemplateSpec templateSpec, IContext context, Writer writer) {
        delegateEngine.process(templateSpec, context, writer);
    }

    @Override
    public IThrottledTemplateProcessor processThrottled(String template, IContext context) {
        return delegateEngine.processThrottled(template, context);
    }

    @Override
    public IThrottledTemplateProcessor processThrottled(String template, Set<String> templateSelectors, IContext context) {
        return delegateEngine.processThrottled(template, templateSelectors, context);
    }

    @Override
    public IThrottledTemplateProcessor processThrottled(TemplateSpec templateSpec, IContext context) {
        return delegateEngine.processThrottled(templateSpec, context);
    }

    @Override
    public String process(String template, BindingModel model) {
        return process(template, ModelContext.wrap(model));
    }
}
