package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.formula.processor.WhereFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    SqlWhereSqm(Collection<String> orderByStatements, TableMetadataHolder metadataHolder, String previousQuery, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata, @NonNull Where root) {
        this.metadataHolder = metadataHolder;
        orderByStatements.forEach(this::addOrderByStatement);
        this.queryBuilder.append(previousQuery);
        this.tableMetadata = tableMetadata;
        this.root = root;
        this.formulaMetadata = (FormulaMetadata) metadataHolder;
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(root.getAttribute(), metadataHolder, tableMetadata));
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
    public SqlWhere limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public SqlWhere offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    @Override
    protected String createQuery() {
        this.queryBuilder.append(processWhere());
        String order = orderStatement();
        this.queryBuilder.append(order);
        if (!order.isEmpty()) {
            this.queryBuilder.append(processLimitOffset());
        }
        return this.queryBuilder.toString();
    }

    @Override
    protected Collection<FormulaProcessor> getNativeQueryProcessor() {
        return formulaMetadata.getNativeQueryProcessor(metadataHolder, tableMetadata);
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
                first = new AdditionWhere(Objects.requireNonNull(first).getOperator(), first.getAttribute(), first.value, first.getTableName(), last.getConditionOperator());
            } else {
                first = new AdditionWhere(Objects.requireNonNull(first).getOperator(), first.getAttribute(), first.value, first.getTableName(), this.additionsWhere.peekFirst().getConditionOperator());
            }
            this.additionsWhere.addFirst(first);
            last = new AdditionWhere(last.getOperator(), last.getAttribute(), last.getValue(), last.getTableName(), ConditionOperator.NONE);
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
            wherePostFix = processor.processWhere(wherePostFix);
        }
        String processedSql = MessageFormat.format(wherePattern, wherePostFix);

        var whereTableFormulas = formulaMetadata.getTableWhereProcessors(metadataHolder, tableMetadata);
        for (var processor : whereTableFormulas) {
            processedSql = processor.processWhere(processedSql);
        }
        return processedSql;
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
        attribute = processAttribute(tableName, attribute);
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
        attribute = processAttribute(tableName, attribute);
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
        attribute = processAttribute(tableName, attribute);
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
        attribute = processAttribute(tableName, attribute);
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
        attribute = processAttribute(tableName, attribute);
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
        attribute = processAttribute(tableName, attribute);
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
        attribute = processAttribute(tableName, attribute);
        return MessageFormat.format(whereColPattern, tableName, colName, attribute.trim());
    }

    private void processEQ(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.EQ)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryEquals(additionWhere.getTableName(), additionWhere.getAttribute(), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }
                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectEquals(additionWhere.getTableName(), additionWhere.getAttribute(), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }
                String processed = processEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }
    }

    private void processGE(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.GE)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryGreaterEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectGreaterEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }

                String processed = processGreaterEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }

    }

    private void processGT(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.GT)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryGreaterThan(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }
                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectGreaterThan(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }

                String processed = processGreaterThan(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }
    }

    private void processLE(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.LE)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryLessEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectLessEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }

                String processed = processLessEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }
    }

    private void processLT(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.LT)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryLessThan(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectLessThan(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }

                String processed = processLessThan(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }
    }

    private void processNEQ(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.NEQ)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryNotEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectNotEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }

                String processed = processNotEquals(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }
    }

    private void processNull(List<String> processedWheres) {
        String isNullPattern = "{0}.{1} IS NULL";
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.NULL)) {
                additionWhere = this.additionsWhere.poll();
                String processed = MessageFormat.format(isNullPattern, additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()));
                processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
            }
        }
    }

    private void processNonNull(List<String> processedWheres) {
        String isNonNullPattern = "{0}.{1} IS NOT NULL";
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.NON_NULL)) {
                additionWhere = this.additionsWhere.poll();
                String processed = MessageFormat.format(isNonNullPattern, additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()));
                processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
            }
        }
    }

    private void processIN(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.IN)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof SqlWhere operation) {
                    String processed = processSubQueryIn(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), operation);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.getValue() instanceof SqlSelect sqlSelect) {
                    String processed = processSubSelectIn(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), sqlSelect);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    continue;
                }

                String processed = processedIn(additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), additionWhere.getAttribute());
                processBinding(processedWheres, processed, additionWhere);
            }
        }
    }

    private void processBetween(List<String> processedWheres) {
        String betweenPattern = "{0}.{1} BETWEEN {2} AND {3}";
        int leftIndex = 0;
        int rightIndex = 0;
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperator().equals(Operator.BETWEEN)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof Object[] casted) {
                    String leftParam = ":betweenP" + leftIndex;
                    String rightParam = ":betweenR" + rightIndex;
                    String processed = MessageFormat.format(betweenPattern, additionWhere.getTableName(), this.metadataHolder.getColumn(additionWhere.getAttribute()), leftParam, rightParam);
                    processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
                    this.queryBindings.add(new QueryBinding(leftParam, casted[0]));
                    this.queryBindings.add(new QueryBinding(rightParam, casted[1]));
                }
            }
            leftIndex++;
            rightIndex++;
        }
    }

    @SuppressWarnings("unchecked")
    private void processBinding(List<String> processedWheres, String processed, SqlWhereSqm.AdditionWhere additionWhere) {
        processedWheres.add(processed + additionWhere.getConditionOperator().getValue());
        String attribute = processAttribute(additionWhere.getTableName(), additionWhere.getAttribute());
        if (additionWhere.getValue() instanceof BaseEntity<?> entity) {
            this.queryBindings.add(new QueryBinding(attribute, entity.getId()));
        } else if (additionWhere.getValue() instanceof Collection<?> objects && objects.stream().allMatch(o -> o instanceof BaseEntity<?>)) {
            Collection<BaseEntity<?>> entities = (Collection<BaseEntity<?>>) additionWhere.getValue();
            this.queryBindings.add(new QueryBinding(attribute, entities.stream().map(BaseEntity::getId).toList()));
        } else {
            this.queryBindings.add(new QueryBinding(attribute, additionWhere.getValue()));
        }
    }

    private String processAttribute(String tableName, String attribute) {
        return tableName.toLowerCase() + "_" + attribute;
    }

    @RequiredArgsConstructor
    @Getter
    private static class AdditionWhere {
        private final Operator operator;
        private final String attribute;
        private final Object value;
        private final String tableName;
        private final ConditionOperator conditionOperator;
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
