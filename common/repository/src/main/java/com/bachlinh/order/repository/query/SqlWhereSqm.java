package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class SqlWhereSqm extends AbstractSql<WhereOperation> implements WhereOperation {

    private final TableMetadataHolder metadataHolder;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final StringBuilder queryBuilder = new StringBuilder();
    private final Where root;
    private final Set<QueryBinding> queryBindings = new HashSet<>();
    private final Queue<AdditionWhere> additionsWhere = new LinkedList<>();

    SqlWhereSqm(Collection<String> orderByStatements, TableMetadataHolder metadataHolder, String previousQuery, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata, Where root) {
        this.metadataHolder = metadataHolder;
        orderByStatements.forEach(this::addOrderByStatement);
        this.queryBuilder.append(previousQuery);
        this.tableMetadata = tableMetadata;
        this.root = root;
    }

    @Override
    public WhereOperation orderBy(OrderBy orderBy) {
        String tableName = this.metadataHolder.getTableName();
        String colName = this.metadataHolder.getColumn(orderBy.getColumn());
        addOrderByStatement(processOrder(tableName, colName, orderBy.getType().name()));
        return this;
    }

    @Override
    public WhereOperation orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table) {
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
    public WhereOperation where(Where where) {
        AdditionWhere additionWhere = new AdditionWhere(where.getOperator(), where.getAttribute(), where.getValue(), metadataHolder.getTableName());
        additionsWhere.add(additionWhere);
        return this;
    }

    @Override
    public WhereOperation where(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        AdditionWhere additionWhere = new AdditionWhere(where.getOperator(), where.getAttribute(), where.getValue(), tableMetadataHolder.getTableName());
        additionsWhere.add(additionWhere);
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
        return this.queryBuilder.toString();
    }

    @Override
    public WhereOperation limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public WhereOperation offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    private String processWhere() {
        List<String> processedWheres = new ArrayList<>();
        String wherePattern = " WHERE {0}";
        String tableName = this.metadataHolder.getTableName();
        AdditionWhere whereRoot = new AdditionWhere(root.getOperator(), root.getAttribute(), root.getValue(), tableName);
        this.additionsWhere.add(whereRoot);

        while (!additionsWhere.isEmpty()) {
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

        FormulaMetadata formulaMetadata = (FormulaMetadata) metadataHolder;
        String[] formula = formulaMetadata.getWhereFormula().split(",");
        for (var f : formula) {
            processedWheres.add(f.trim());
        }
        String wherePostFix = String.join(", ", processedWheres.toArray(new String[0]));
        if (wherePostFix.endsWith(", ")) {
            wherePostFix = wherePostFix.substring(0, wherePostFix.length() - 2);
        }
        return MessageFormat.format(wherePattern, wherePostFix);
    }

    private String processSubSelectIn(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} IN {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection.getNativeQuery());
    }

    private String processSubQueryIn(String tableName, String colName, WhereOperation operation) {
        String whereSubQueryPattern = "{0}.{1} IN {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery());
    }

    private String processedIn(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} IN :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private String processSubSelectNotEquals(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} != {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection.getNativeQuery());
    }

    private String processSubQueryNotEquals(String tableName, String colName, WhereOperation operation) {
        String whereSubQueryPattern = "{0}.{1} != {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery());
    }

    private String processNotEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} != :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private String processSubSelectLessEquals(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} <= {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection.getNativeQuery());
    }

    private String processSubQueryLessEquals(String tableName, String colName, WhereOperation operation) {
        String whereSubQueryPattern = "{0}.{1} <= {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery());
    }

    private String processLessEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} <= :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private String processSubSelectLessThan(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} < {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection.getNativeQuery());
    }

    private String processSubQueryLessThan(String tableName, String colName, WhereOperation operation) {
        String whereSubQueryPattern = "{0}.{1} < {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery());
    }

    private String processLessThan(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} < :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private String processSubSelectGreaterThan(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} > {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection.getNativeQuery());
    }

    private String processSubQueryGreaterThan(String tableName, String colName, WhereOperation operation) {
        String whereSubQueryPattern = "{0}.{1} > {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, operation.getNativeQuery());
    }

    private String processGreaterThan(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} > :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private String processSubSelectGreaterEquals(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} >= {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection);
    }

    private String processSubQueryGreaterEquals(String tableName, String colName, WhereOperation subQuery) {
        String whereSubQueryPattern = "{0}.{1} >= {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, subQuery.getNativeQuery());
    }

    private String processGreaterEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} >= :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private String processSubSelectEquals(String tableName, String colName, SqlSelection sqlSelection) {
        String subSelectPattern = "{0}.{1} = {2}";
        return MessageFormat.format(subSelectPattern, tableName, colName, sqlSelection.getNativeQuery());
    }

    private String processSubQueryEquals(String tableName, String colName, WhereOperation subQuery) {
        String whereSubQueryPattern = "{0}.{1} = {2}";
        return MessageFormat.format(whereSubQueryPattern, tableName, colName, subQuery.getNativeQuery());
    }

    private String processEquals(String tableName, String colName, String attribute) {
        String whereColPattern = "{0}.{1} = :{2}";
        return MessageFormat.format(whereColPattern, tableName, colName, attribute);
    }

    private void processEQ(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.EQ)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }
                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }
                String processed = processEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
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
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryGreaterEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectGreaterEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }

                String processed = processGreaterEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }

    }

    private void processGT(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.GT)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryGreaterThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }
                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectGreaterThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }

                String processed = processGreaterThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processLE(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.LE)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryLessEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectLessEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }

                String processed = processLessEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processLT(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.LT)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryLessThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectLessThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }

                String processed = processLessThan(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
                this.queryBindings.add(new QueryBinding(additionWhere.attribute(), additionWhere.value()));
            }
        }
    }

    private void processNEQ(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.NEQ)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryNotEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectNotEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }

                String processed = processNotEquals(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
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
                processedWheres.add(processed);
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
                processedWheres.add(processed);
            }
        }
    }

    private void processIN(List<String> processedWheres) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.operator().equals(Operator.IN)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.value() instanceof WhereOperation operation) {
                    String processed = processSubQueryIn(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), operation);
                    processedWheres.add(processed);
                    this.queryBindings.addAll(operation.getQueryBindings());
                    continue;
                }

                if (additionWhere.value() instanceof SqlSelection sqlSelection) {
                    String processed = processSubSelectIn(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), sqlSelection);
                    processedWheres.add(processed);
                    continue;
                }

                String processed = processedIn(additionWhere.tableName(), this.metadataHolder.getColumn(additionWhere.attribute()), additionWhere.attribute());
                processedWheres.add(processed);
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
                    processedWheres.add(processed);
                    this.queryBindings.add(new QueryBinding("bt1", casted[0]));
                    this.queryBindings.add(new QueryBinding("bt2", casted[1]));
                }
            }
        }
    }

    private record AdditionWhere(Operator operator, String attribute, Object value, String tableName) {

    }
}
