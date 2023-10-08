package com.bachlinh.order.entity.repository.query.mssql;

import com.bachlinh.order.entity.repository.query.FunctionDialect;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class MssqlFunctionDialect implements FunctionDialect {

    @Override
    public Collection<String> getAllAvailableFunction() {
        Set<String> results = new LinkedHashSet<>();
        results.add(count());
        results.add(min());
        results.add(max());
        results.add(sum());
        return results;
    }

    @Override
    public String count() {
        return "COUNT";
    }

    @Override
    public String min() {
        return "MIN";
    }

    @Override
    public String max() {
        return "MAX";
    }

    @Override
    public String sum() {
        return "SUM";
    }
}
