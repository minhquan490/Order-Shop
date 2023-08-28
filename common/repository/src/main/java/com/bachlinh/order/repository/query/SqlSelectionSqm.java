package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@RequiredArgsConstructor
class SqlSelectionSqm extends AbstractSql<SqlSelect> implements SqlSelect {
    private static final String ALIAS_PATTERN = "%s AS '%s'";

    private final TableMetadataHolder targetMetadata;
    private final FormulaMetadata formulaMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final Queue<SelectHolder> selects = new LinkedList<>();
    private final Queue<OtherColumnTableSelect> otherSelects = new LinkedList<>();
    private final Queue<FormulaProcessor> otherSelectFormulas = new LinkedList<>();
    private String tableAlias;

    @Override
    public SqlSelect select(Select select) {
        select = QueryUtils.resolveSelectAlias(select, targetMetadata);
        String column = targetMetadata.getColumn(select.getColumn());
        if (select.getAlias() != null) {
            column = String.format(ALIAS_PATTERN, column, select.getAlias());
        }
        this.selects.add(new SelectHolder(select.getColumn(), column));
        return this;
    }

    @Override
    public SqlSelect select(Select select, String functionName) {
        String functionPattern = "%s(%s)";
        select = QueryUtils.resolveSelectAlias(select, targetMetadata);
        String column = String.format(functionPattern, targetMetadata.getColumn(select.getColumn()), functionName);
        if (select.getAlias() != null) {
            column = String.format(ALIAS_PATTERN, column, select.getAlias());
        }
        this.selects.add(new SelectHolder(select.getColumn(), column));
        return this;
    }

    @Override
    public SqlSelect select(Select select, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        if (!StringUtils.hasText(select.getAlias())) {
            select = Select.builder().column(select.getColumn()).alias(tableMetadataHolder.getTableName().concat(".").concat(tableMetadataHolder.getColumn(select.getColumn()))).build();
        }
        OtherColumnTableSelect otherColumnTableSelect = new OtherColumnTableSelect(tableMetadataHolder.getTableName(), tableMetadataHolder.getColumn(select.getColumn()), select.getAlias());
        otherSelects.add(otherColumnTableSelect);
        FormulaMetadata formulaMetadata1 = (FormulaMetadata) tableMetadataHolder;
        otherSelectFormulas.addAll(formulaMetadata1.getColumnSelectProcessors(select.getColumn(), tableMetadataHolder, this.tableMetadata));
        return this;
    }

    @Override
    public SqlSelect orderBy(OrderBy orderBy) {
        String tableName = this.targetMetadata.getTableName();
        String colName = this.targetMetadata.getColumn(orderBy.getColumn());
        addOrderByStatement(processOrder(tableName, colName, orderBy.getType().name()));
        return this;
    }

    @Override
    public SqlSelect orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        String colName = tableMetadataHolder.getColumn(orderBy.getColumn());
        OtherOrderBy order = new OtherOrderBy(tableMetadataHolder.getTableName(), colName, orderBy.getType().name());
        addOtherOrderByStatement(order);
        return this;
    }

    @Override
    public SqlWhere where(Where where) {
        return new SqlWhereSqm(combineOrderBy(), this.targetMetadata, selectFromQuery(), this.tableMetadata, where);
    }

    @Override
    public SqlWhere where(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder metadataHolder = this.tableMetadata.get(table);
        return new SqlWhereSqm(combineOrderBy(), metadataHolder, selectFromQuery(), this.tableMetadata, where);
    }

    @Override
    public SqlJoin join(Join join) {
        var joinSqm = new SqlJoinSqm(this.targetMetadata, this.tableMetadata, selectFromQuery(), new LinkedList<>(combineOrderBy()), formulaMetadata);
        return joinSqm.join(join);
    }

    @Override
    public SqlJoin join(Join join, NativeQueryHolder subQuery, String subQueryAlias, String currentColumnRef) {
        var joinSqm = new SqlJoinSqm(this.targetMetadata, this.tableMetadata, selectFromQuery(), new LinkedList<>(combineOrderBy()), formulaMetadata);
        return joinSqm.join(join, subQuery, subQueryAlias, currentColumnRef);
    }

    @Override
    public String getNativeQuery() {
        String order = orderStatement();
        String nativeQuery;
        if (!order.isEmpty()) {
            nativeQuery = selectFromQuery() + order + processLimitOffset();
        } else {
            nativeQuery = selectFromQuery() + order;
        }
        if (nativeQuery.endsWith(", ")) {
            nativeQuery = nativeQuery.substring(0, nativeQuery.length() - 2);
        }
        var processors = formulaMetadata.getTableProcessors(targetMetadata, tableMetadata);
        for (var processor : processors) {
            nativeQuery = processor.process(nativeQuery);
        }
        return nativeQuery;
    }

    @Override
    public Collection<QueryBinding> getQueryBindings() {
        return Collections.emptyList();
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    private String selectFromQuery() {
        String selectPattern = "SELECT {0} FROM {1}";
        String columnSelectPattern = "{0}.{1}";
        String zeroPositionParam;

        if (selects.isEmpty() && otherSelects.isEmpty()) {
            zeroPositionParam = this.targetMetadata.getColumn("*");
        } else {
            List<String> processedCols = new ArrayList<>(this.selects.size() + this.otherSelects.size());
            List<String> formulaProcessed = new LinkedList<>();

            while (!this.selects.isEmpty()) {
                var selectHolder = selects.poll();
                String select = selectHolder.select();
                String processedCol = MessageFormat.format(columnSelectPattern, this.targetMetadata.getTableName(), select);
                processedCols.add(processedCol);
                var processors = formulaMetadata.getColumnSelectProcessors(selectHolder.fieldName(), this.targetMetadata, this.tableMetadata);
                processors.forEach(selectFormulaProcessor -> formulaProcessed.add(selectFormulaProcessor.processSelect(select, this.targetMetadata, this.tableMetadata)));
            }

            while (!this.otherSelects.isEmpty()) {
                var otherSelect = otherSelects.poll();
                String processedCol = MessageFormat.format(columnSelectPattern, otherSelect.tableName(), otherSelect.columnName());
                if (StringUtils.hasText(otherSelect.alias())) {
                    processedCol = String.format(ALIAS_PATTERN, processedCol, otherSelect.alias());
                }
                processedCols.add(processedCol);
                while (!this.otherSelectFormulas.isEmpty()) {
                    var processor = this.otherSelectFormulas.poll();
                    String processed = processor.process(processedCol);
                    formulaProcessed.add(processed);
                }
            }

            processedCols.addAll(formulaProcessed);
            zeroPositionParam = String.join(", ", processedCols.toArray(new String[0]));
        }

        String tableName = this.targetMetadata.getTableName();

        if (StringUtils.hasText(this.tableAlias)) {
            tableName = String.format(ALIAS_PATTERN, tableName, this.tableAlias);
        }

        var processors = formulaMetadata.getTableSelectProcessors(targetMetadata, tableMetadata);
        for (var processor : processors) {
            zeroPositionParam = processor.processSelect(zeroPositionParam, targetMetadata, tableMetadata);
        }

        if (zeroPositionParam.trim().endsWith(",")) {
            zeroPositionParam = zeroPositionParam.substring(0, zeroPositionParam.length() - 2);
        }

        return MessageFormat.format(selectPattern, zeroPositionParam, tableName);
    }

    @Override
    public SqlSelect limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public SqlSelect offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    private record OtherColumnTableSelect(String tableName, String columnName, String alias) {
    }

    private record SelectHolder(String fieldName, String select) {

    }
}
