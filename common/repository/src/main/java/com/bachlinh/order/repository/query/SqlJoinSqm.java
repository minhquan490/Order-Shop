package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@RequiredArgsConstructor
class SqlJoinSqm extends AbstractSql<JoinOperation> implements JoinOperation {

    private final TableMetadataHolder targetMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final String selectQuery;
    private final Queue<String> orderByStatements;
    private final StringBuilder queryBuilder = new StringBuilder();
    private final Queue<String> joinStatements = new LinkedList<>();

    @Override
    public JoinOperation join(Join join) {
        String joinTypeTemplate = "{0} JOIN";
        JoinMetadataHolder joinMetadataHolder = (JoinMetadataHolder) targetMetadata;
        var joinMetadata = joinMetadataHolder.getJoin(join.getAttribute());
        var statement = joinMetadata.joinStatement();
        if (statement.contains("{0}")) {
            statement = MessageFormat.format(statement, MessageFormat.format(joinTypeTemplate, join.getType().name()));
        } else {
            statement = MessageFormat.format(joinTypeTemplate, join.getType().name()) + statement;
        }
        joinStatements.add(statement);
        return this;
    }

    @Override
    public JoinOperation join(Join join, NativeQueryHolder subQuery, String currentColumnRef) {
        String template = "{0} {1} ON {2}.{3} = {4}.{5}";
        String joinTypeTemplate = "{0} JOIN";
        String sub = subQuery.getNativeQuery();
        String[] subQueryParts = sub.split(" ");
        if (!subQueryParts[subQueryParts.length - 2].equals("AS")) {
            throw new PersistenceException("Sub query must has alias");
        }
        String subQueryAlias = subQueryParts[subQueryParts.length - 1];
        String currentTable = this.targetMetadata.getTableName();
        String joinStatement = MessageFormat.format(template, MessageFormat.format(joinTypeTemplate, join.getType().name()), subQueryAlias, subQueryAlias, join.getAttribute(), currentTable, currentColumnRef);
        joinStatements.add(joinStatement);
        return this;
    }

    @Override
    public WhereOperation where(Where where) {
        return new SqlWhereSqm(orderByStatements, this.targetMetadata, combineQuery(), this.tableMetadata, where);
    }

    @Override
    public WhereOperation where(Where where, Class<? extends AbstractEntity<?>> table) {
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
        return queryBuilder.toString();
    }

    @Override
    public JoinOperation orderBy(OrderBy orderBy) {
        String tableName = this.targetMetadata.getTableName();
        String colName = this.targetMetadata.getColumn(orderBy.getColumn());
        this.orderByStatements.add(processOrder(tableName, colName, orderBy.getType().name()));
        return this;
    }

    @Override
    public JoinOperation orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        String colName = tableMetadataHolder.getColumn(orderBy.getColumn());
        OtherOrderBy order = new OtherOrderBy(tableMetadataHolder.getTableName(), colName, orderBy.getType().name());
        addOtherOrderByStatement(order);
        return this;
    }

    @Override
    public JoinOperation limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public JoinOperation offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    private String combineQuery() {
        String combinedJoin = String.join(" ", joinStatements.toArray(new String[0]));
        return String.join(" ", selectQuery, combinedJoin);
    }
}
