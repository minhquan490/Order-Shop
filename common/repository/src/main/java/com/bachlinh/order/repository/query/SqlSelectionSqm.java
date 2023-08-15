package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@RequiredArgsConstructor
class SqlSelectionSqm extends AbstractSql<SqlSelection> implements SqlSelection {
    private static final String ALIAS_PATTERN = "%s AS '%s'";

    private final TableMetadataHolder targetMetadata;
    private final FormulaMetadata formulaMetadata;
    private final Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata;
    private final Queue<String> selects = new LinkedList<>();
    private final Queue<OtherColumnTableSelect> otherSelects = new LinkedList<>();
    private String tableAlias;

    @Override
    public SqlSelection select(Select select) {
        if (!StringUtils.hasText(select.getAlias())) {
            select = Select.builder().column(select.getColumn()).alias(targetMetadata.getTableName().concat(".").concat(targetMetadata.getColumn(select.getColumn()))).build();
        }
        String column = targetMetadata.getColumn(select.getColumn());
        if (select.getAlias() != null) {
            column = String.format(ALIAS_PATTERN, column, select.getAlias());
        }
        this.selects.add(column);
        return this;
    }

    @Override
    public SqlSelection select(Select select, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        if (!StringUtils.hasText(select.getAlias())) {
            select = Select.builder().column(select.getColumn()).alias(tableMetadataHolder.getTableName().concat(".").concat(tableMetadataHolder.getColumn(select.getColumn()))).build();
        }
        OtherColumnTableSelect otherColumnTableSelect = new OtherColumnTableSelect(tableMetadataHolder.getTableName(), tableMetadataHolder.getColumn(select.getColumn()), select.getAlias());
        otherSelects.add(otherColumnTableSelect);
        return this;
    }

    @Override
    public SqlSelection orderBy(OrderBy orderBy) {
        String tableName = this.targetMetadata.getTableName();
        String colName = this.targetMetadata.getColumn(orderBy.getColumn());
        addOrderByStatement(processOrder(tableName, colName, orderBy.getType().name()));
        return this;
    }

    @Override
    public SqlSelection orderBy(OrderBy orderBy, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder tableMetadataHolder = this.tableMetadata.get(table);
        String colName = tableMetadataHolder.getColumn(orderBy.getColumn());
        OtherOrderBy order = new OtherOrderBy(tableMetadataHolder.getTableName(), colName, orderBy.getType().name());
        addOtherOrderByStatement(order);
        return this;
    }

    @Override
    public WhereOperation where(Where where) {
        return new SqlWhereSqm(combineOrderBy(), this.targetMetadata, selectFromQuery(), this.tableMetadata, where);
    }

    @Override
    public WhereOperation where(Where where, Class<? extends AbstractEntity<?>> table) {
        TableMetadataHolder metadataHolder = this.tableMetadata.get(table);
        return new SqlWhereSqm(combineOrderBy(), metadataHolder, selectFromQuery(), this.tableMetadata, where);
    }

    @Override
    public JoinOperation join(Join join) {
        var joinSqm = new SqlJoinSqm(this.targetMetadata, this.tableMetadata, selectFromQuery(), new LinkedList<>(combineOrderBy()));
        return joinSqm.join(join);
    }

    @Override
    public JoinOperation join(Join join, NativeQueryHolder subQuery, String currentColumnRef) {
        var joinSqm = new SqlJoinSqm(this.targetMetadata, this.tableMetadata, selectFromQuery(), new LinkedList<>(combineOrderBy()));
        return joinSqm.join(join, subQuery, currentColumnRef);
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
        return nativeQuery;
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

            while (!this.selects.isEmpty()) {
                String select = selects.poll();
                String processedCol = MessageFormat.format(columnSelectPattern, this.targetMetadata.getTableName(), select);
                processedCols.add(processedCol);
            }

            while (!this.otherSelects.isEmpty()) {
                var otherSelect = otherSelects.poll();
                String processedCol = MessageFormat.format(columnSelectPattern, otherSelect.tableName(), otherSelect.columnName());
                if (StringUtils.hasText(otherSelect.alias())) {
                    processedCol = String.format(ALIAS_PATTERN, processedCol, otherSelect.alias());
                }
                processedCols.add(processedCol);
            }

            zeroPositionParam = String.join(" ,", processedCols.toArray(new String[0]));
        }

        String tableName = this.targetMetadata.getTableName();

        if (StringUtils.hasText(this.tableAlias)) {
            tableName = String.format(ALIAS_PATTERN, tableName, this.tableAlias);
        }

        zeroPositionParam = String.format(formulaMetadata.getSelectFormula(), zeroPositionParam);

        if (zeroPositionParam.endsWith(",")) {
            zeroPositionParam = zeroPositionParam.substring(0, zeroPositionParam.length() - 1);
        }

        return MessageFormat.format(selectPattern, zeroPositionParam, tableName);
    }

    @Override
    public SqlSelection limit(long limit) {
        super.internalLimit(limit);
        return this;
    }

    @Override
    public SqlSelection offset(long offset) {
        super.internalOffset(offset);
        return this;
    }

    private record OtherColumnTableSelect(String tableName, String columnName, String alias) {
    }
}
