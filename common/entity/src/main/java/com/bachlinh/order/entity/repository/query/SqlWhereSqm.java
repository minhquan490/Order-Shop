package com.bachlinh.order.entity.repository.query;

import com.google.common.base.Objects;

import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.function.ReturnedCallback;
import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.formula.processor.WhereFormulaProcessor;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.utils.QueryUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.lang.NonNull;

/**
 * The sql where clause processor. Process {@link Where} object to native query for query engine execute.
 * This processor will convert {@code WHERE} clause metadata in {@link Where} object to native query by using string
 * process algorithm, time complexity N^3.
 *
 * @author MinhQuan
 */
class SqlWhereSqm extends AbstractSql<SqlWhere> implements SqlWhere {

    private final FormulaMetadata formulaMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final StringBuilder queryBuilder = new StringBuilder();
    private final RootHolder root;
    private final Set<QueryBinding> queryBindings = new HashSet<>();
    private final Deque<AdditionWhere> additionsWhere = new LinkedList<>();
    private final Set<WhereFormulaProcessor> whereFormulaProcessors = new HashSet<>();

    /**
     * Create new instance to begin building native sql.
     *
     * @param orderByStatements All order by statement built at {@link SqlSelect}.
     * @param metadataHolder    Metadata of entity, contains table name and all available column of entity.
     * @param previousQuery     The previous query if had.
     * @param tableMetadata     All metadata of all entities for support condition apply on other table.
     * @param root              {@link Where} root of this processor.
     */
    SqlWhereSqm(Collection<String> orderByStatements, TableMetadataHolder metadataHolder, String previousQuery, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata, @NonNull Where root) {
        super(metadataHolder);
        orderByStatements.forEach(this::addOrderByStatement);
        this.queryBuilder.append(previousQuery);
        this.tableMetadata = tableMetadata;
        this.root = new RootHolder(root);
        this.formulaMetadata = (FormulaMetadata) metadataHolder;
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(root.getAttribute(), metadataHolder, tableMetadata));
    }

    @Override
    public SqlWhere orderBy(OrderBy orderBy) {
        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) ((EntityContext) getTargetMetadata()).getEntity().getClass();

        return orderBy(orderBy, table);
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
        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) ((EntityContext) getTargetMetadata()).getEntity().getClass();

        return and(where, table);
    }

    @Override
    public SqlWhere and(Where where, Class<? extends AbstractEntity<?>> table) {
        return onOperator(where, table, ConditionOperator.AND);
    }

    @Override
    public SqlWhere or(Where where) {
        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) ((EntityContext) getTargetMetadata()).getEntity().getClass();

        return or(where, table);
    }

    @Override
    public SqlWhere or(Where where, Class<? extends AbstractEntity<?>> table) {
        return onOperator(where, table, ConditionOperator.OR);
    }

    @Override
    public SqlWhereAppender appendAnd(Where where) {
        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) ((EntityContext) getTargetMetadata()).getEntity().getClass();

        return appendAnd(where, table);
    }

    @Override
    public SqlWhereAppender appendAnd(Where where, Class<? extends AbstractEntity<?>> table) {
        return appendOperator(where, table, ConditionOperator.AND);
    }

    @Override
    public SqlWhereAppender appendOr(Where where) {
        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) ((EntityContext) getTargetMetadata()).getEntity().getClass();

        return appendOr(where, table);
    }

    @Override
    public SqlWhereAppender appendOr(Where where, Class<? extends AbstractEntity<?>> table) {
        return appendOperator(where, table, ConditionOperator.OR);
    }

    @Override
    public SqlWhere limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    public SqlWhere offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    @Override
    protected String createQuery() {
        this.queryBuilder.append(" ");
        this.queryBuilder.append(QueryUtils.prepareQuery(processWhere()));
        String order = orderStatement();
        this.queryBuilder.append(order);
        if (!order.isEmpty()) {
            this.queryBuilder.append(processLimitOffset());
        }
        return this.queryBuilder.toString();
    }

    @Override
    protected Collection<FormulaProcessor> getNativeQueryProcessor() {
        return formulaMetadata.getNativeQueryProcessor(getTargetMetadata(), tableMetadata);
    }

    /**
     * Process convert all {@link Where} objects to query string, then append it after {@code WHERE} keyword.
     * This method also append query pieces create by processor specify by {@link Formula} annotation.
     *
     * @return Processed query contains all main conditions and all formulas apply to selecting table.
     */
    private String processWhere() {
        List<String> processedWheres = new ArrayList<>();
        String wherePattern = " WHERE {0}";
        String tableName = this.getTargetMetadata().getTableName();

        addWhereRoot(tableName);

        while (!additionsWhere.isEmpty() && this.root != null) {
            switch (this.root.getRoot().getOperation()) {
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

        String processedSql = processWhereFieldFormula(processedWheres, wherePattern);

        return processWhereTableFormula(processedSql);
    }

    /**
     * Append {@link Where} root to head of condition query. Appendage root only occur when the {@link Where} root is not dirty.
     *
     * @param tableName Table for appendage root apply on.
     */
    private void addWhereRoot(String tableName) {
        if (root != null && !root.isTouched()) {
            AdditionWhere whereRoot = new AdditionWhere(root.getRoot().getOperation(), root.getRoot().getAttribute(), root.getRoot().getValue(), tableName, ConditionOperator.NONE);
            this.additionsWhere.addFirst(whereRoot);
        }
    }

    private String processWhereFieldFormula(List<String> processedWheres, String wherePattern) {
        String wherePostFix = String.join(" ", processedWheres.toArray(new String[0]));
        for (var processor : whereFormulaProcessors) {
            wherePostFix = processor.processWhere(wherePostFix);
        }
        return MessageFormat.format(wherePattern, wherePostFix);
    }

    private String processWhereTableFormula(String processedSql) {
        var whereTableFormulas = formulaMetadata.getTableWhereProcessors(getTargetMetadata(), tableMetadata);
        for (var processor : whereTableFormulas) {
            processedSql = processor.processWhere(processedSql);
        }
        return processedSql.replace("  ", " ");
    }

    private String processSubSelectIn(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, "IN", true);
    }

    private String processSubQueryIn(String tableName, String colName, SqlWhere operation) {
        return processWhereOperationQuery(tableName, colName, operation, "IN", true);
    }

    private String processedIn(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, "IN", false);
    }

    private String processSubSelectNotEquals(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, "!=", true);
    }

    private String processSubQueryNotEquals(String tableName, String colName, SqlWhere operation) {
        return processWhereOperationQuery(tableName, colName, operation, "!=", true);
    }

    private String processNotEquals(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, "!=", false);
    }

    private String processSubSelectLessEquals(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, "<=", true);
    }

    private String processSubQueryLessEquals(String tableName, String colName, SqlWhere operation) {
        return processWhereOperationQuery(tableName, colName, operation, "<=", true);
    }

    private String processLessEquals(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, "<=", false);
    }

    private String processSubSelectLessThan(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, "<", true);
    }

    private String processSubQueryLessThan(String tableName, String colName, SqlWhere operation) {
        return processWhereOperationQuery(tableName, colName, operation, "<", true);
    }

    private String processLessThan(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, "<", false);
    }

    private String processSubSelectGreaterThan(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, ">", true);
    }

    private String processSubQueryGreaterThan(String tableName, String colName, SqlWhere operation) {
        return processWhereOperationQuery(tableName, colName, operation, ">", true);
    }

    private String processGreaterThan(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, ">", false);
    }

    private String processSubSelectGreaterEquals(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, ">=", true);
    }

    private String processSubQueryGreaterEquals(String tableName, String colName, SqlWhere subQuery) {
        return processWhereOperationQuery(tableName, colName, subQuery, ">=", true);
    }

    private String processGreaterEquals(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, ">=", false);
    }

    private String processSubSelectEquals(String tableName, String colName, SqlSelect sqlSelect) {
        return processWhereOperationQuery(tableName, colName, sqlSelect, "=", true);
    }

    private String processSubQueryEquals(String tableName, String colName, SqlWhere subQuery) {
        return processWhereOperationQuery(tableName, colName, subQuery, "=", true);
    }

    private String processEquals(String tableName, String colName, String attribute) {
        DummyNativeQueryHolder dummyHolder = new DummyNativeQueryHolder(attribute, tableName);
        return processWhereOperationQuery(tableName, colName, dummyHolder, "=", false);
    }

    private void processEQ(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryEquals((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectEquals((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processEquals((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.EQ, subQueryCallback, subSelectCallback, callback);
    }

    private void processGE(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryGreaterEquals((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectGreaterEquals((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processGreaterEquals((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.GE, subQueryCallback, subSelectCallback, callback);
    }

    private void processGT(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryGreaterThan((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectGreaterThan((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processGreaterThan((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.GT, subQueryCallback, subSelectCallback, callback);
    }

    private void processLE(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryLessEquals((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectLessEquals((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processLessEquals((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.LE, subQueryCallback, subSelectCallback, callback);
    }

    private void processLT(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryLessThan((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectLessThan((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processLessThan((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.LT, subQueryCallback, subSelectCallback, callback);
    }

    private void processNEQ(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryNotEquals((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectNotEquals((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processNotEquals((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.NEQ, subQueryCallback, subSelectCallback, callback);
    }

    private void processIN(List<String> processedWheres) {
        ReturnedCallback subQueryCallback = params -> processSubQueryIn((String) params[0], (String) params[1], (SqlWhere) params[2]);
        ReturnedCallback subSelectCallback = params -> processSubSelectIn((String) params[0], (String) params[1], (SqlSelect) params[2]);
        ReturnedCallback callback = params -> processedIn((String) params[0], this.getTargetMetadata().getColumn((String) params[1]), (String) params[2]);

        onOperation(processedWheres, Operation.IN, subQueryCallback, subSelectCallback, callback);
    }

    private void processNull(List<String> processedWheres) {
        String isNullPattern = "{0}.{1} IS NULL";
        onProcessPattern(processedWheres, isNullPattern, Operation.NULL);
    }

    private void processNonNull(List<String> processedWheres) {
        String isNonNullPattern = "{0}.{1} IS NOT NULL";
        onProcessPattern(processedWheres, isNonNullPattern, Operation.NON_NULL);
    }

    private void processBetween(List<String> processedWheres) {
        String betweenPattern = "{0}.{1} BETWEEN {2} AND {3}";
        int leftIndex = 0;
        int rightIndex = 0;
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperation().equals(Operation.BETWEEN)) {
                additionWhere = this.additionsWhere.poll();
                if (additionWhere.getValue() instanceof Object[] casted) {
                    String leftParam = ":betweenP" + leftIndex;
                    String rightParam = ":betweenR" + rightIndex;
                    String processed = MessageFormat.format(betweenPattern, java.util.Objects.requireNonNull(additionWhere).getTableName(), this.getTargetMetadata().getColumn(additionWhere.getAttribute()), leftParam, rightParam);
                    processedWheres.add(catProcessedQueryAndOperation(processed, additionWhere.getConditionOperator()));
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
        processedWheres.add(catProcessedQueryAndOperation(processed, additionWhere.getConditionOperator()));
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

    private void onWhereProcessing(AdditionWhere additionWhere, List<String> processedWheres, Supplier<String> subQueryCallback, Supplier<String> subSelectCallback, Supplier<String> callback) {
        if (additionWhere instanceof AdditionWhereAppender additionWhereAppender) {
            String template = "(%s) %s";
            List<String> groupEQPieces = new LinkedList<>();
            Set<AdditionWhere> children = additionWhereAppender.getChildren();
            if (children.isEmpty()) {
                onProcessBinding(additionWhere, processedWheres, callback);
            } else {
                onProcessBinding(additionWhere, groupEQPieces, callback);

                for (AdditionWhere add : children) {
                    onProcessBinding(add, groupEQPieces, callback);
                }

                String queryPieces = String.join(" ", groupEQPieces.toArray(new String[0])).trim();
                queryPieces = QueryUtils.prepareQuery(queryPieces);
                queryPieces = String.format(template, queryPieces, additionWhereAppender.getConditionOperator().getValue());
                processedWheres.add(queryPieces);
            }
        } else {
            onSqlWhere(additionWhere, processedWheres, subQueryCallback);

            onSqlSelect(additionWhere, processedWheres, subSelectCallback);

            onProcessBinding(additionWhere, processedWheres, callback);
        }
    }

    private void onProcessBinding(AdditionWhere additionWhere, List<String> processedWheres, Supplier<String> callback) {
        String processed = callback.get();
        processBinding(processedWheres, processed, additionWhere);
    }

    private void onSqlWhere(AdditionWhere additionWhere, List<String> processedWheres, Supplier<String> processQueryCallback) {
        if (additionWhere.getValue() instanceof SqlWhere operation) {
            String processed = processQueryCallback.get();
            processedWheres.add(catProcessedQueryAndOperation(processed, additionWhere.getConditionOperator()));
            this.queryBindings.addAll(operation.getQueryBindings());
        }
    }

    private void onSqlSelect(AdditionWhere additionWhere, List<String> processedWheres, Supplier<String> selectCallback) {
        if (additionWhere.getValue() instanceof SqlSelect) {
            String processed = selectCallback.get();
            processedWheres.add(catProcessedQueryAndOperation(processed, additionWhere.getConditionOperator()));
        }
    }

    private SqlWhereAppender appendOperator(Where where, Class<? extends AbstractEntity<?>> table, ConditionOperator operator) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        AdditionWhere previous = this.additionsWhere.pollLast();
        if (previous == null) {
            previous = new AdditionWhere(root.getRoot().getOperation(), root.getRoot().getAttribute(), root.getRoot().getValue(), tableMetadataHolder.getTableName(), operator);
            root.setTouched(true);
        }
        if (previous instanceof AdditionWhereAppender previousAppender) {
            AdditionWhere additionWhere = new AdditionWhere(where.getOperation(), where.getAttribute(), where.getValue(), tableMetadataHolder.getTableName(), operator);
            previousAppender.append(additionWhere);
            additionsWhere.add(previousAppender);
        } else {
            AdditionWhereAppender appender = new AdditionWhereAppender(previous.getOperation(), previous.getAttribute(), previous.getValue(), previous.getTableName(), previous.getConditionOperator());
            AdditionWhere additionWhere = new AdditionWhere(where.getOperation(), where.getAttribute(), where.getValue(), tableMetadataHolder.getTableName(), operator);
            appender.append(additionWhere);
            additionsWhere.add(appender);
        }
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(where.getAttribute(), tableMetadataHolder, tableMetadata));
        return this;
    }

    private SqlWhere onOperator(Where where, Class<? extends AbstractEntity<?>> table, ConditionOperator operator) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        AdditionWhere additionWhere = new AdditionWhere(where.getOperation(), where.getAttribute(), where.getValue(), tableMetadataHolder.getTableName(), operator);
        additionsWhere.add(additionWhere);
        this.whereFormulaProcessors.addAll(formulaMetadata.getColumnWhereProcessors(where.getAttribute(), tableMetadataHolder, tableMetadata));
        return this;
    }

    private void onOperation(List<String> processedWheres, Operation operation, ReturnedCallback subQueryCallback, ReturnedCallback subSelectCallback, ReturnedCallback callback) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperation().equals(operation)) {
                additionWhere = this.additionsWhere.poll();

                String tableName = java.util.Objects.requireNonNull(additionWhere).getTableName();
                String attribute = additionWhere.getAttribute();
                Object value = additionWhere.getValue();

                Supplier<String> subQuerySupplier = () -> (String) subQueryCallback.execute(tableName, this.getTargetMetadata().getColumn(attribute), value);

                Supplier<String> subSelectSupplier = () -> (String) subSelectCallback.execute(tableName, this.getTargetMetadata().getColumn(attribute), value);

                Supplier<String> callbackSupplier = () -> (String) callback.execute(tableName, attribute, attribute);

                onWhereProcessing(additionWhere, processedWheres, subQuerySupplier, subSelectSupplier, callbackSupplier);
            }
        }
    }

    private void onProcessPattern(List<String> processedWheres, String pattern, Operation operation) {
        while (!this.additionsWhere.isEmpty()) {
            var additionWhere = this.additionsWhere.peek();
            if (additionWhere.getOperation().equals(operation)) {
                additionWhere = this.additionsWhere.poll();
                String processed = MessageFormat.format(pattern, java.util.Objects.requireNonNull(additionWhere).getTableName(), this.getTargetMetadata().getColumn(additionWhere.getAttribute()));
                processedWheres.add(catProcessedQueryAndOperation(processed, additionWhere.getConditionOperator()));
            }
        }
    }

    private String catProcessedQueryAndOperation(String processedQuery, ConditionOperator operator) {
        return operator.getValue() + processedQuery;
    }

    private String processWhereOperationQuery(String tableName, String colName, NativeQueryHolder nativeQueryHolder, String logicOperator, boolean wrapped) {
        String formatted;
        if (wrapped) {
            String wrappedRawPattern = "{0}.{1} %s ({2})";
            formatted = wrappedRawPattern.formatted(logicOperator);
        } else {
            String unwrappedRawPattern = "{0}.{1} %s :{2}";
            formatted = unwrappedRawPattern.formatted(logicOperator);
        }
        return MessageFormat.format(formatted, tableName, colName, nativeQueryHolder.getNativeQuery().trim());
    }

    private class DummyNativeQueryHolder implements NativeQueryHolder {

        private final String attribute;
        private final String tableName;

        private DummyNativeQueryHolder(String attribute, String tableName) {
            this.attribute = attribute;
            this.tableName = tableName;
        }

        @Override
        public String getNativeQuery() {
            return SqlWhereSqm.this.processAttribute(getTableName(), getAttribute());
        }

        @Override
        public Collection<QueryBinding> getQueryBindings() {
            return Collections.emptyList();
        }

        public String getAttribute() {
            return attribute;
        }

        public String getTableName() {
            return tableName;
        }
    }

    private static class AdditionWhere {
        private final Operation operation;
        private final String attribute;
        private final Object value;
        private final String tableName;
        private final ConditionOperator conditionOperator;

        public AdditionWhere(Operation operation, String attribute, Object value, @NonNull String tableName, ConditionOperator conditionOperator) {
            this.operation = operation;
            this.attribute = attribute;
            this.value = value;
            this.tableName = tableName;
            this.conditionOperator = conditionOperator;
        }

        public Operation getOperation() {
            return this.operation;
        }

        public String getAttribute() {
            return this.attribute;
        }

        public Object getValue() {
            return this.value;
        }

        @NonNull
        public String getTableName() {
            return this.tableName;
        }

        public ConditionOperator getConditionOperator() {
            return this.conditionOperator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AdditionWhere that = (AdditionWhere) o;
            return getOperation() == that.getOperation() && Objects.equal(getAttribute(), that.getAttribute()) && Objects.equal(getValue(), that.getValue()) && Objects.equal(getTableName(), that.getTableName()) && getConditionOperator() == that.getConditionOperator();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getOperation(), getAttribute(), getValue(), getTableName(), getConditionOperator());
        }
    }

    private static class AdditionWhereAppender extends AdditionWhere {
        private final Set<AdditionWhere> children = new HashSet<>();

        public AdditionWhereAppender(Operation operation, String attribute, Object value, String tableName, ConditionOperator conditionOperator) {
            super(operation, attribute, value, tableName, conditionOperator);
        }

        public void append(AdditionWhere additionWhere) {
            children.add(additionWhere);
        }

        public Set<AdditionWhere> getChildren() {
            return this.children;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            AdditionWhereAppender that = (AdditionWhereAppender) o;
            return com.google.common.base.Objects.equal(getChildren(), that.getChildren());
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(super.hashCode(), getChildren());
        }
    }

    private static class RootHolder {
        private final Where root;

        private boolean isTouched = false;

        public RootHolder(Where root) {
            this.root = root;
        }

        public Where getRoot() {
            return this.root;
        }

        public boolean isTouched() {
            return this.isTouched;
        }

        public void setTouched(boolean isTouched) {
            this.isTouched = isTouched;
        }
    }

    private enum ConditionOperator {
        AND(" AND "),
        OR(" OR "),
        NONE(""); // for root and tail only

        private final String value;

        ConditionOperator(String s) {
            this.value = s;
        }

        public String getValue() {
            return this.value;
        }
    }
}
