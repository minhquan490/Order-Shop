package com.bachlinh.order.mail.template;

import org.thymeleaf.context.IContext;

import java.util.Locale;
import java.util.Set;

class ModelContext implements IContext {
    private final BindingModel model;

    static ModelContext wrap(BindingModel model) {
        return new ModelContext(model);
    }

    private ModelContext(BindingModel model) {
        this.model = model;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public boolean containsVariable(String name) {
        return model.containsKey(name);
    }

    @Override
    public Set<String> getVariableNames() {
        return model.keySet();
    }

    @Override
    public Object getVariable(String name) {
        return model.get(name);
    }
}
