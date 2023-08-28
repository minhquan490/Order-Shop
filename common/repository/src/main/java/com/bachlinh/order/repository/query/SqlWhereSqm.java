package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.WhereFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SqlWhereSqm extends AbstractSql<SqlWhere> implements SqlWhere {

    private final TableMetadataHolder metadataHolder;
    private final FormulaMetadata formulaMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final StringBuilder queryBuilder = new StringBuilder();
    private final Where root;
    private final Set<QueryBinding> queryBindings = new HashSet<>();
    private final Deque<AdditionWhere> additionsWhere = new LinkedList<>();
    private final Set<WhereFormulaProcessor> whereFormulaProcessors = new HashSet<>();

    SqlWhereSqm(Collection<String> orderByStatements, TableMetadataHolder metadataHolder, String previousQuery, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata, @Nullable Where root) {
        this.metadataHolder = metadataHolder;
        orderByStatements.forEach(this::addOrderByStatement);
        this.queryBuilder.append(previousQuery);
        this.tableMetadata = tableMetadata;
        this.root = root;
        this.formulaMetadata = (FormulaMetadata) metadataHolder;
    }

    @Override
    public SqlWhere orderBy(OrderBy orderBy) {
        String tableName = this.metadataHolder.getTableName();
        String colName = this.metadataHolder.getColumn(orderBy.getColumn());
        addOrderByStatement(processOrder(tableName, colName, orderBy.getType().name()));
        return this;
    }

    @Override
    public SqlWhere orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        String colName = tableMetadataHolder.getColumn(orderBy.getColumn());
        OtherOrderBy order = new OtherOrderBy(tableMetadataHolder.getTableName(), colName, orderBy.getType().name());
        addOtherOrderByStatement(order);
        return this;
    }

    @Override
    public Collection<QueryBinding> getQueryBindings() {
        return this.queryBindings;
    }

    @Override
    public SqlWhere where(Where where) {
        return and(where);
    }

    @Override
    public SqlWhere where(Where where, Class<? extends AbstractEntity<?>> table) {
        return and(where, table);
    }

    @Override
    public SqlWhere and(Where where) {
        AdditionWhere additionWhere = new AdditionWhere(where.getOperator(), where.getAttribute(), where.getValue(), metadataHolder.getTableName(), ConditionOperator.AND);
        additionsWhere.add(additionWhere);
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(where.getAttribute(), metadataHolder, tableMetadata));
        return this;
    }

    @Override
    public SqlWhere and(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        AdditionWhere additionWhere = new AdditionWhere(where.getOperator(), where.getAttribute(), where.getValue(), tableMetadataHolder.getTableName(), ConditionOperator.AND);
        additionsWhere.add(additionWhere);
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(where.getAttribute(), tableMetadataHolder, tableMetadata));
        return this;
    }

    @Override
    public SqlWhere or(Where where) {
        AdditionWhere additionWhere = new AdditionWhere(where.getOperator(), where.getAttribute(), where.getValue(), metadataHolder.getTableName(), ConditionOperator.OR);
        additionsWhere.add(additionWhere);
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(where.getAttribute(), metadataHolder, tableMetadata));
        return this;
    }

    @Override
    public SqlWhere or(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        AdditionWhere additionWhere = new AdditionWhere(where.getOperator(), where.getAttribute(), where.getValue(), tableMetadataHolder.getTableName(), ConditionOperator.OR);
        additionsWhere.add(additionWhere);
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(where.getAttribute(), tableMetadataHolder, tableMetadata));
        return this;
    }

    @Override
    public String getNativeQuery() {
        this.queryBuilder.append(processWhere());
        String order = orderStatement();
        this.queryBuilder.append(order);
        if (!order.isEmpty()) {
            this.queryBuilder.append(processLimitOffset());
        }
        String query = this.queryBuilder.toString();
        var processors = formulaMetadata.getTableProcessors(metadataHolder, tableMetadata);
        for (var processor : processors) {
            query = processor.process(query);
        }
        return query;
    }

    @Override
    public SqlWhere limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public SqlWhere offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    private String processWhere() {
        List<String> processedWheres = new ArrayList<>();
        String wherePattern = " WHERE {0}";
        String tableName = this.metadataHolder.getTableName();
        if (root != null) {
            AdditionWhere whereRoot = new AdditionWhere(root.getOperator(), root.getAttribute(), root.getValue(), tableName, ConditionOperator.NONE);
            this.additionsWhere.addFirst(whereRoot);
        }
        if (this.additionsWhere.size() > 1) {
            var last = this.additionsWhere.pollLast();
            var first = this.additionsWhere.pollFirst();
            if (this.additionsWhere.isEmpty()) {
                first = new AdditionWhere(first.operator(), first.attribute(), first.value, first.tableName(), last.conditionOperator());
            } else {
                first = new AdditionWhere(first.operator(), first.attribute(), first.value, first.tableName(), this.additionsWhere.peekFirst().conditionOperator());
            }
            this.additionsWhere.addFirst(first);
            last = new AdditionWhere(last.operator(), last.attribute(), last.value(), last.tableName(), ConditionOperator.NONE);
            this.additionsWhere.addLast(last);
        }
        while (!additionsWhere.isEmpty() && this.root != null) {
            switch (this.root.getOperator()) {
                case EQ -> processEQ(processedWheres);
                case GE -> processGE(processedWheres);
                case GT -> processGT(processedWheres);
                case LE -> processLE(processedWheres);
                case LT -> processLT(processedWheres);
                case NEQ -> processNEQ(processedWheres);
                case NULL -> processNull(processedWheres);
                case NON_NULL -> processNonNull(processedWheres);
                case IN -> processIN(processedWheres);
                case BETWEEN -> processBetween(processedWheres);
            }
        }

        String wherePostFix = String.join(" ", processedWheres.toArray(new String[0]));
        for (var processor : whereFormulaProcessors) {
            wherePostFix = processor.processWhere(wherePostFix, metadataHolder, tableMetadata);
        }
        return MessageFormat.format(wherePattern, wherePostFix);
    }

    private String processSubSelectIn(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} IN {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryIn(String tableName, String colName, SqlWhere operation) {
        String whereSubQueryPattern = "{0}.{1} IN {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery().trim());
    }

    private String processedIn(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} IN :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private String processSubSelectNotEquals(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} != {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryNotEquals(String tableName, String colName, SqlWhere operation) {
        String whereSubQueryPattern = "{0}.{1} != {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery().trim());
    }

    private String processNotEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} != :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private String processSubSelectLessEquals(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} <= {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryLessEquals(String tableName, String colName, SqlWhere operation) {
        String whereSubQueryPattern = "{0}.{1} <= {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery().trim());
    }

    private String processLessEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} <= :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private String processSubSelectLessThan(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} < {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryLessThan(String tableName, String colName, SqlWhere operation) {
        String whereSubQueryPattern = "{0}.{1} < {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery().trim());
    }

    private String processLessThan(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} < :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private String processSubSelectGreaterThan(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} > {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryGreaterThan(String tableName, String colName, SqlWhere operation) {
        String whereSubQueryPattern = "{0}.{1} > {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery().trim());
    }

    private String processGreaterThan(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} > :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private String processSubSelectGreaterEquals(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} >= {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryGreaterEquals(String tableName, String colName, SqlWhere subQuery) {
        String whereSubQueryPattern = "{0}.{1} >= {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, subQuery.getNativeQuery().trim());
    }

    private String processGreaterEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} >= :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private String processSubSelectEquals(String tableName, String colName, SqlSelect sqlSelect) {
        String subSelectPattern = "{0}.{1} = {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelect.getNativeQuery().trim());
    }

    private String processSubQueryEquals(String tableName, String colName, SqlWhere subQuery) {
        String whereSubQueryPattern = "{0}.{1} = {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, subQuery.getNativeQuery().trim());
    }

    private String processEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} = :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private void processEQ(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.EQ)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryEquals(additionWhere.tableName(), additionWhere.attribute(), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }
                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectEquals(additionWhere.tableName(), additionWhere.attribute(), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }
                String processed = processEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                if (additionWhere.value() instanceof BaseEntity<?> entity) {
                    this.queryBindings.add(new QueryBinding(additionWhere.attribute(), entity.getId()));
                } else {
                    this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
                }
            }
        }
    }

    private void processGE(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.GE)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryGreaterEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectGreaterEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }

                String processed = processGreaterEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }

    }

    private void processGT(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.GT)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryGreaterThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }
                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectGreaterThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }

                String processed = processGreaterThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processLE(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.LE)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryLessEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectLessEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }

                String processed = processLessEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processLT(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.LT)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryLessThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectLessThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }

                String processed = processLessThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processNEQ(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.NEQ)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryNotEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectNotEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }

                String processed = processNotEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                if (additionWhere.value() instanceof BaseEntity<?> entity) {
                    this.queryBindings.add(new QueryBinding(additionWhere.attribute(), entity.getId()));
                } else {
                    this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
                }
            }
        }
    }

    private void processNull(List<String> processedWheres) {
        String isNullPattern = "{0}.{1} IS NULL";
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.NULL)) {
                additionWhere = this.additionsWhere.poll();
                String processed = MessageFormat.format(isNullPattern, additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()));
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
            }
        }
    }

    private void processNonNull(List<String> processedWheres) {
        String isNonNullPattern = "{0}.{1} IS NOT NULL";
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.NON_NULL)) {
                additionWhere = this.additionsWhere.poll();
                String processed = MessageFormat.format(isNonNullPattern, additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()));
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
            }
        }
    }

    private void processIN(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.IN)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof SqlWhere operation) {
                    String processed = processSubQueryIn(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectIn(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    continue;
                }

                String processed = processedIn(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processBetween(List<String> processedWheres) {
        String betweenPattern = "{0}.{1} BETWEEN :bt1 AND :bt2";
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.BETWEEN)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof Object[] casted) {
                    String processed = MessageFormat.format(betweenPattern, additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()));
                    processedWheres.add(processed + additionWhere.conditionOperator().getValue());
                    this.queryBindings.add(new QueryBinding("bt1", casted[0]));
                    this.queryBindings.add(new QueryBinding("bt2", casted[1]));
                }
            }
        }
    }

    private record AdditionWhere(@NonNull Operator operator, String attribute, Object value, String tableName,
                                 @NonNull ConditionOperator conditionOperator) {

    }

    @Getter
    private enum ConditionOperator {
        AND(" AND "),
        OR(" OR "),
        NONE(""); // for root and tail only

        private final String value;

        ConditionOperator(String s) {
            this.value = s;
        }
    }
}
