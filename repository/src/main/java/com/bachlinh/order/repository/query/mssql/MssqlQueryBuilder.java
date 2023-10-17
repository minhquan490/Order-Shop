package com.bachlinh.order.repository.query.mssql;

import com.bachlinh.order.repository.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.repository.query.AbstractSqlBuilder;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlUpdate;

import java.util.Map;

/**
 * {@link SqlBuilder} implementation for Mssql database.
 *
 * @author MinhQuan
 * */
public class  MssqlQueryBuilder extends AbstractSqlBuilder implements SqlBuilder {

    public MssqlQueryBuilder(Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tableMetadata) {
        super(tableMetadata);
    }

    @Override
    protected SqlSelect doCreateSqlSelect(TableMetadataHolder holder, FormulaMetadata formulaMetadata) {
        return new MssqlSelect(holder, (FormulaMetadata) holder, getTableMetadata());
    }

    @Override
    protected SqlUpdate doCreateSqlUpdate(TableMetadataHolder metadataHolder) {
        return new MssqlUpdate(metadataHolder);
    }
}
