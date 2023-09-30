package com.bachlinh.order.entity.formula;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.adapter.SelectFormulaAdapter;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.repository.query.FunctionDialect;
import com.bachlinh.order.entity.utils.QueryUtils;
import org.springframework.core.Ordered;

import java.util.Map;

public class CommonFieldSelectFormula extends SelectFormulaAdapter {
    private static final String PATTERN = "%s.%s AS '%s.%s'";
    private final FunctionDialect functionDialect;

    public CommonFieldSelectFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        this.functionDialect = FunctionDialect.getDialect(environment.getProperty("server.database.driver"));
    }

    @Override
    protected boolean shouldApply(String sql) {
        return !QueryUtils.isQueryStartWithFunction(sql, functionDialect);
    }

    @Override
    protected String doProcess(String sql) {
        String tableName = getTargetTable().getTableName();
        return String.format(PATTERN, tableName, "CREATED_BY", tableName, "CREATED_BY") +
                ", " +
                String.format(PATTERN, tableName, "MODIFIED_BY", tableName, "MODIFIED_BY") +
                ", " +
                String.format(PATTERN, tableName, "CREATED_DATE", tableName, "CREATED_DATE") +
                ", " +
                String.format(PATTERN, tableName, "MODIFIED_DATE", tableName, "MODIFIED_DATE") +
                ", " +
                sql;
    }

    @Override
    public int order() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new CommonFieldSelectFormula(resolver, targetTable, tables);
    }

}
