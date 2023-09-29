package com.bachlinh.order.entity.formula;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.formula.adapter.WhereFormulaAdapter;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.core.container.DependenciesResolver;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.MessageFormat;
import java.util.Map;

public class ProductEnableFormula extends WhereFormulaAdapter {
    private static final String PATTERN = "%s, %s";

    public ProductEnableFormula(DependenciesResolver resolver, TableMetadataHolder tableMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadataHolders) {
        super(resolver, tableMetadata, tableMetadataHolders);
    }

    @Override
    protected String doProcess(String sql) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (authentication == null) {
                return processFormula(sql);
            } else {
                var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
                if (!authorities.contains(Role.ADMIN.name())) {
                    return processFormula(sql);
                }
            }
        } catch (Exception e) {
            // Do nothing
        }
        return sql;
    }

    @Override
    public int order() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public FormulaProcessor getInstance(DependenciesResolver resolver, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return new ProductEnableFormula(resolver, targetTable, tables);
    }

    private String processFormula(String sql) {
        String enableSelect = "{0} = '1'";
        String column = getTargetTable().getColumn(Product_.ENABLED);
        String select = MessageFormat.format(enableSelect, column);
        return String.format(PATTERN, sql, select);
    }
}
