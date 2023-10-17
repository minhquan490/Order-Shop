package com.bachlinh.order.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

/**
 * A sealed update sql processor, use for support generate {@code UPDATE} statement
 * by detect changed columns and generate {@code UPDATE} query for these.
 * This interface only permit {@link AbstractSqlUpdate} implement. If you want create
 * new implement for other database, please {@code extends} it.
 *
 * @author MinhQuan
 * */
public sealed interface SqlUpdate extends NativeQueryHolder permits AbstractSqlUpdate {

    /**
     * Self creation for generate {@code UPDATE} statement by read the changes of columns
     * in entity/table then generate statement for it.
     *
     * @param entity Table for detect change.
     * @return Self of this object.
     * */
    SqlUpdate update(AbstractEntity<?> entity);
}
