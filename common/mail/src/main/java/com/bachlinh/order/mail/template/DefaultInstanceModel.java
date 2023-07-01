package com.bachlinh.order.mail.template;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class DefaultInstanceModel extends HashMap<String, Object> implements BindingModel {

    @Override
    public Map<String, Object> getBindingObjects() {
        return entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
