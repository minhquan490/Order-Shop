package com.bachlinh.order.entity.formula.processor;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.service.container.DependenciesResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractFormulaProcessor implements FormulaProcessor {
    private final DependenciesResolver dependenciesResolver;

    @Override
    public final String process(String sql) {
        return doProcess(sql);
    }

    protected abstract String doProcess(String sql);

    protected abstract TableMetadataHolder getTargetTable();

    protected abstract Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> getTables();
}
