package com.bachlinh.order.repository.query;

import com.bachlinh.order.repository.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.repository.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.utils.QueryUtils;

import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class AbstractSqlSelection extends AbstractSql<SqlSelect> implements SqlSelect {

    private final FormulaMetadata formulaMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final Queue<SelectHolder> selects = new LinkedList<>();
    private final Queue<FormulaProcessor> formulaProcessors = new LinkedList<>();
    private String tableAlias;

    protected AbstractSqlSelection(TableMetadataHolder targetMetadata, FormulaMetadata formulaMetadata, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        super(targetMetadata);
        this.formulaMetadata = formulaMetadata;
        this.tableMetadata = tableMetadata;
    }

    @Override
    public SqlSelect select(Select select) {
        Class<? extends AbstractEntity<?>> table = getCurrentTable();

        return select(select, table);
    }

    @Override
    public SqlSelect select(Select select, String functionName) {

        Class<? extends AbstractEntity<?>> table = getCurrentTable();

        return select(select, table, functionName);
    }

    @Override
    public SqlSelect select(Select select, Class<? extends AbstractEntity<?>> table) {
        return select(select, table, null);
    }

    @Override
    public SqlSelect orderBy(OrderBy orderBy) {
        Class<? extends AbstractEntity<?>> table = getCurrentTable();
        return orderBy(orderBy, table);
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
        return new SqlWhereSqm(combineOrderBy(), getTargetMetadata(), selectFromQuery(), this.tableMetadata, where);
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

    @Override
    public Collection<QueryBinding> getQueryBindings() {
        return Collections.emptyList();
    }

    @Override
    public SqlJoin join(Join join) {
        var joinSqm = getJoinInstance();
        return joinSqm.join(join);
    }

    @Override
    public SqlJoin join(Join join, NativeQueryHolder subQuery, String subQueryAlias, String currentColumnRef) {
        var joinSqm = getJoinInstance();
        return joinSqm.join(join, subQuery, subQueryAlias, currentColumnRef);
    }

    @Override
    public SqlWhere where(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder metadataHolder = this.tableMetadata.get(table);
        return new SqlWhereSqm(combineOrderBy(), metadataHolder, selectFromQuery(), this.tableMetadata, where);
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    @Override
    protected String createQuery() {
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
        return nativeQuery;
    }

    @Override
    protected Collection<FormulaProcessor> getNativeQueryProcessor() {
        return formulaMetadata.getNativeQueryProcessor(getTargetMetadata(), tableMetadata);
    }

    protected abstract FunctionDialect getFunctionDialect();

    protected abstract String getSelectRecordPattern();

    protected String getTableAlias() {
        return this.tableAlias;
    }

    private SqlSelect select(Select select, Class<? extends AbstractEntity<?>> table, String functionName) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        String column;

        if (StringUtils.hasText(functionName)) {
            String functionPattern = "%s(%s)";
            column = String.format(functionPattern, functionName, getTargetMetadata().getColumn(select.getColumn()));
        } else {
            column = getTargetMetadata().getColumn(select.getColumn());
        }

        if (select.getAlias() != null) {
            column = String.format(getSelectRecordPattern(), column, select.getAlias());
        }

        this.selects.add(new SelectHolder(select.getColumn(), column));
        FormulaMetadata formulaMetadata1 = (FormulaMetadata) tableMetadataHolder;
        formulaProcessors.addAll(formulaMetadata1.getColumnSelectProcessors(select.getColumn(), tableMetadataHolder, this.tableMetadata));
        return this;
    }

    private SqlJoin getJoinInstance() {
        return new SqlJoinSqm(getTargetMetadata(), this.tableMetadata, selectFromQuery(), new LinkedList<>(combineOrderBy()), formulaMetadata);
    }

    private String selectFromQuery() {
        String selectPattern = "SELECT {0} FROM {1}";
        String columnSelectPattern = "{0}.{1}";
        String zeroPositionParam;

        if (selects.isEmpty()) {
            zeroPositionParam = getTargetMetadata().getColumn("*");
        } else {
            List<String> processedCols = new ArrayList<>(this.selects.size());
            List<String> formulaProcessed = new LinkedList<>();

            processSelect(columnSelectPattern, processedCols, formulaProcessed);

            processedCols.addAll(formulaProcessed);
            zeroPositionParam = String.join(", ", processedCols.toArray(new String[0]));
        }

        String tableName = getTargetMetadata().getTableName();

        if (StringUtils.hasText(getTableAlias())) {
            tableName = String.format(getSelectRecordPattern(), tableName, getTableAlias());
        }

        var processors = formulaMetadata.getTableSelectProcessors(getTargetMetadata(), tableMetadata);
        for (var processor : processors) {
            zeroPositionParam = processor.processSelect(zeroPositionParam);
        }

        if (zeroPositionParam.trim().endsWith(",")) {
            zeroPositionParam = zeroPositionParam.substring(0, zeroPositionParam.length() - 2);
        }

        return MessageFormat.format(selectPattern, zeroPositionParam, tableName);
    }

    private void processSelect(String columnSelectPattern, List<String> processedCols, List<String> formulaProcessed) {
        while (!this.selects.isEmpty()) {
            var selectHolder = selects.poll();
            String select = selectHolder.select();
            String processedCol;
            if (QueryUtils.isQueryStartWithFunction(select, getFunctionDialect())) {
                processedCol = select;
            } else {
                processedCol = MessageFormat.format(columnSelectPattern, getTargetMetadata().getTableName(), select);
            }
            processedCols.add(processedCol);

            for (var processor : this.formulaProcessors) {
                String processed = processor.process(processedCol);
                formulaProcessed.add(processed);
            }
        }
    }

    private record SelectHolder(String fieldName, String select) {

    }
}
