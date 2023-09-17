package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.adapter.SelectFormulaAdapter;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.query.FunctionDialect;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.core.Ordered;

import java.text.MessageFormat;
import java.util.Map;

public class IdFieldFormula extends SelectFormulaAdapter {

    private final FunctionDialect functionDialect;

    private IdFieldFormula(DependenciesResolver dependenciesResolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        super(dependenciesResolver, targetTable, tables);
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        this.functionDialect = FunctionDialect.getDialect(environment.getProperty("server.database.driver"));
    }

    @Override
    protected boolean shouldApply(String sql) {
        return !QueryUtils.isQueryStartWithFunction(sql, functionDialect);
    }

    @Override
    protected String doProcess(String sql) {
        String asTemplate = "%s AS '%s'";
        String template = "{0}, {1}";
        String idColumnName = getTargetTable().getTableName() + "." + getTargetTable().getColumn("id");
        idColumnName = String.format(asTemplate, idColumnName, idColumnName);
        if (!sql.contains(idColumnName)) {
            return MessageFormat.format(template, idColumnName, sql);
        }
        return sql;
    }

    @Override
    public int order() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new IdFieldFormula(resolver, targetTable, tables);
    }
}
