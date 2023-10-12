package com.bachlinh.order.entity.repository.query;

import com.bachlinh.order.entity.model.AbstractEntity;

/**
 * The query builder support build native sql query by using only java code base.
 *
 * @author MinhQuan
 * */
public interface SqlBuilder {

    /**
     * Create {@link SqlSelect} processor for process {@code SELECT} clause of sql query.
     *
     * @param table Entity table for processor apply on.
     * @return {@link SqlSelect} Select processor support {@code SELECT} any columns available.
     * */
    SqlSelect from(Class<? extends AbstractEntity<?>> table);

    /**
     * Create {@link SqlSelect} processor for process {@code SELECT} clause of sql query.
     * This method support alias {@code AS} will be applied to table.
     *
     * @param table Entity table for processor apply on.
     * @param alias Alias of table.
     * @return {@link SqlSelect} Select processor support {@code SELECT} any columns available.
     * @see #from(Class)
     * */
    SqlSelect from(Class<? extends AbstractEntity<?>> table, String alias);

    /**
     * Create {@link SqlUpdate} update query processor for {@code UPDATE} table.
     *
     * @param table Table for {@code UPDATE} statement generation.
     * @return {@code UPDATE} statement processor support generate update sql by using java code base.
     * */
    SqlUpdate updateQueryFor(Class<? extends AbstractEntity<?>> table);

    /**
     * Create {@link SqlDelete} delete query processor for {@code DELETE} table.
     *
     * @param table Table for extract id property from it to support delete query generation.
     * @return {@code DELETE} statement processor support generate delete sql by using java code base.
     * */
    SqlDelete deleteQueryFor(Class<? extends AbstractEntity<?>> table);
}
