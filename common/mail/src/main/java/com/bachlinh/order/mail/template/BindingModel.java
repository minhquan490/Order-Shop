package com.bachlinh.order.mail.template;

import java.util.Map;

public interface BindingModel extends Map<String, Object> {
    Map<String, Object> getBindingObjects();

    static BindingModel getInstance() {
        return new DefaultInstanceModel();
    }
}
