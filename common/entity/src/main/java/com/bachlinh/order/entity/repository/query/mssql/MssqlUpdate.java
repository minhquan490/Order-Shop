package com.bachlinh.order.entity.repository.query.mssql;

import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.repository.query.AbstractSqlUpdate;

public class MssqlUpdate extends AbstractSqlUpdate {

    protected MssqlUpdate(TableMetadataHolder metadataHolder) {
        super(metadataHolder);
    }

    @Override
    protected String getUpdatePattern() {
        return "UPDATE {0} WITH(UPDLOCK, ROWLOCK) SET {1} WHERE ID = {2}";
    }
}
