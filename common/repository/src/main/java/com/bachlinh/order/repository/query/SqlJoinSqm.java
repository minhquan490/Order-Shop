package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.JoinFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@RequiredArgsConstructor
class SqlJoinSqm extends AbstractSql<SqlJoin> implements SqlJoin {
    private static final String JOIN_TYPE_TEMPLATE = "{0} JOIN";

    private final TableMetadataHolder targetMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final String selectQuery;
    private final Queue<String> orderByStatements;
    private final FormulaMetadata formulaMetadata;
    private final StringBuilder queryBuilder = new StringBuilder();
    private final Queue<String> joinStatements = new LinkedList<>();
    private final List<QueryBinding> subQueryParam = new LinkedList<>();
    private final List<JoinFormulaProcessor> joinFormulaProcessors = new LinkedList<>();

    @Override
    public SqlJoin join(Join join) {
        JoinMetadataHolder joinMetadataHolder = (JoinMetadataHolder) targetMetadata;
        return getSqlJoin(join, joinMetadataHolder);
    }

    @Override
    public SqlJoin join(Join join, Class<? extends AbstractEntity<?>> table) {
        JoinMetadataHolder joinMetadataHolder = (JoinMetadataHolder) tableMetadata.get(table);
        return getSqlJoin(join, joinMetadataHolder);
    }

    @Override
    public SqlJoin join(Join join, NativeQueryHolder subQuery, String subQueryAlias, String currentColumnRef) {
        String template = "{0} {1} ON {2}.{3} = {4}.{5}";
        String subQueryTemplate = "({0}) {1}";
        String sub = subQuery.getNativeQuery();
        String currentTable = this.targetMetadata.getTableName();
        String joinStatement = MessageFormat.format(template, MessageFormat.format(JOIN_TYPE_TEMPLATE, join.getType().name()), MessageFormat.format(subQueryTemplate, sub, subQueryAlias), subQueryAlias, join.getAttribute(), currentTable, currentColumnRef);
        joinStatements.add(joinStatement);
        subQueryParam.addAll(subQuery.getQueryBindings());
        return this;
    }

    @Override
    public SqlWhere where(Where where) {
        return new SqlWhereSqm(orderByStatements, this.targetMetadata, combineQuery(), this.tableMetadata, where);
    }

    @Override
    public SqlWhere where(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder metadataHolder = this.tableMetadata.get(table);
        return new SqlWhereSqm(orderByStatements, metadataHolder, combineQuery(), this.tableMetadata, where);
    }

    @Override
    public String getNativeQuery() {
        queryBuilder.append(combineQuery());
        String order = orderStatement();
        queryBuilder.append(order);
        if (!order.isEmpty()) {
            queryBuilder.append(processLimitOffset());
        }
        String query = queryBuilder.toString();
        var processors = formulaMetadata.getTableProcessors(targetMetadata, tableMetadata);
        for (var processor : processors) {
            query = processor.process(query);
        }
        return query;
    }

    @Override
    public Collection<QueryBinding> getQueryBindings() {
        return this.subQueryParam;
    }

    @Override
    public SqlJoin orderBy(OrderBy orderBy) {
        String tableName = this.targetMetadata.getTableName();
        String colName = this.targetMetadata.getColumn(orderBy.getColumn());
        this.orderByStatements.add(processOrder(tableName, colName, orderBy.getType().name()));
        return this;
    }

    @Override
    public SqlJoin orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        String colName = tableMetadataHolder.getColumn(orderBy.getColumn());
        OtherOrderBy order = new OtherOrderBy(tableMetadataHolder.getTableName(), colName, orderBy.getType().name());
        addOtherOrderByStatement(order);
        return this;
    }

    @Override
    public SqlJoin limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public SqlJoin offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    private String combineQuery() {
        String combinedJoin = String.join(" ", joinStatements.toArray(new String[0]));
        for (var processor : joinFormulaProcessors) {
            combinedJoin = processor.processJoin(combinedJoin, targetMetadata, tableMetadata);
        }
        var tableFormula = formulaMetadata.getTableJoinProcessors(targetMetadata, tableMetadata);
        for (var processor : tableFormula) {
            combinedJoin = processor.processJoin(combinedJoin, targetMetadata, tableMetadata);
        }
        return String.join(" ", selectQuery, combinedJoin);
    }

    @NonNull
    private SqlJoin getSqlJoin(Join join, JoinMetadataHolder joinMetadataHolder) {
        var joinMetadata = joinMetadataHolder.getJoin(join.getAttribute());
        var statement = joinMetadata.joinStatement();
        if (statement.contains("{0}")) {
            statement = MessageFormat.format(statement, MessageFormat.format(JOIN_TYPE_TEMPLATE, join.getType().name()));
        } else {
            statement = MessageFormat.format(SqlJoinSqm.JOIN_TYPE_TEMPLATE, join.getType().name()) + statement;
        }
        joinStatements.add(statement);
        Collection<JoinFormulaProcessor> processors = formulaMetadata.getColumnJoinProcessors(join.getAttribute(), this.targetMetadata, this.tableMetadata);
        joinFormulaProcessors.addAll(processors);
        return this;
    }
}
