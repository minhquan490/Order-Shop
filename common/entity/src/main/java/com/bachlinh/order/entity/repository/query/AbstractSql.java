package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.formula.processor.FormulaProcessor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class AbstractSql<T> implements SqlOrderBy<T>, SqlLimitOffset<T>, NativeQueryHolder {

    private final Queue<String> orderByStatements = new LinkedList<>();
    private final Queue<OtherOrderBy> otherOrderByStatements = new LinkedList<>();
    private long limit = -1;
    private long offset = -1;

    @Override
    public final String getNativeQuery() {
        String processedQuery = createQuery();
        var processors = getNativeQueryProcessor();
        for (var processor : processors) {
            processedQuery = processor.process(processedQuery);
        }
        return processedQuery;
    }

    protected void internalLimit(long limit) {
        this.limit = limit;
    }

    protected void internalOffset(long offset) {
        this.offset = offset;
    }

    protected String processLimitOffset() {
        if (limit >= 1 && offset < 0) {
            offset = 0;
        }
        return processOffset() + processLimit();
    }

    private String processLimit() {
        String template = " FETCH NEXT {0} ROWS ONLY";
        if (limit >= 1) {
            return MessageFormat.format(template, limit);
        }
        return "";
    }

    private String processOffset() {
        String template = " OFFSET {0} ROWS";
        if (offset >= 0) {
            return MessageFormat.format(template, offset);
        }
        return "";
    }

    protected String processOrder(String tableName, String colName, String type) {
        String orderColPattern = "{0}.{1} {2}";
        return MessageFormat.format(orderColPattern, tableName, colName, type);
    }

    protected void addOrderByStatement(String orderByStatement) {
        this.orderByStatements.add(orderByStatement);
    }

    protected void addOtherOrderByStatement(OtherOrderBy orderBy) {
        this.otherOrderByStatements.add(orderBy);
    }

    protected String orderStatement() {
        String orderByStatementPattern = " ORDER BY {0}";
        if (orderByStatements.isEmpty() && otherOrderByStatements.isEmpty()) {
            return "";
        } else {
            Collection<String> processedOrder = combineOrderBy();

            String orderBy = String.join(", ", processedOrder.toArray(new String[0]));
            return MessageFormat.format(orderByStatementPattern, orderBy);
        }
    }

    protected Collection<String> combineOrderBy() {
        List<String> processedOrder = new ArrayList<>(this.orderByStatements.size() + this.otherOrderByStatements.size());

        while (!this.orderByStatements.isEmpty()) {
            String orderByStatement = this.orderByStatements.poll();
            processedOrder.add(orderByStatement);
        }

        while (!this.otherOrderByStatements.isEmpty()) {
            var otherOrderBy = this.otherOrderByStatements.poll();
            String processed = processOrder(otherOrderBy.tableName(), otherOrderBy.columnName(), otherOrderBy.type());
            processedOrder.add(processed);
        }
        return processedOrder;
    }

    protected abstract String createQuery();

    protected abstract Collection<FormulaProcessor> getNativeQueryProcessor();

    protected record OtherOrderBy(String tableName, String columnName, String type) {
    }
}
