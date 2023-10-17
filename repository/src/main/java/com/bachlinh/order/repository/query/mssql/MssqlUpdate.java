package com.bachlinh.order.repository.query.mssql;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.repository.query.AbstractSqlUpdate;
import com.bachlinh.order.repository.query.SqlUpdate;

/**
 * {@link SqlUpdate} implementation for Mssql database.
 *
 * @author MinhQuan
 * */
public class MssqlUpdate extends AbstractSqlUpdate {

    /**
     * Create processor instance for table will be updated.
     *
     * @param metadataHolder Metadata of table, contains table name and all columns of table.
     * */
    MssqlUpdate(TableMetadataHolder metadataHolder) {
        super(metadataHolder);
    }

    @Override
    protected String getUpdatePattern() {
        return "UPDATE {0} WITH(UPDLOCK, ROWLOCK) SET {1} WHERE ID = {2}";
    }
}
