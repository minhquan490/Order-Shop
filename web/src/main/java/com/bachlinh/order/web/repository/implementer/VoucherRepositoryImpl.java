package com.bachlinh.order.web.repository.implementer;


import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.VoucherRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RepositoryComponent
@ActiveReflection
public class VoucherRepositoryImpl extends AbstractRepository<String, Voucher> implements VoucherRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public VoucherRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Voucher.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Voucher saveVoucher(Voucher voucher) {
        return save(voucher);
    }

    @Override
    public Voucher updateVoucher(Voucher voucher) {
        return save(voucher);
    }

    @Override
    public Voucher getVoucher(@NonNull Collection<Select> selects, @NonNull Collection<Join> joins, @NonNull Collection<Where> wheres) {
        var results = new ArrayList<>(getVouchers(selects, joins, wheres));
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public Voucher getVoucherById(@NonNull Collection<Select> selects, String id) {
        var idWhere = Where.builder().attribute(Voucher_.ID).value(id).operation(Operation.EQ).build();
        return getVoucher(selects, Collections.emptyList(), Collections.singletonList(idWhere));
    }

    @Override
    public Voucher getVoucherForUpdate(String id) {
        Select idSelect = Select.builder().column(Voucher_.ID).build();
        Select nameSelect = Select.builder().column(Voucher_.NAME).build();
        Select discountPercentSelect = Select.builder().column(Voucher_.DISCOUNT_PERCENT).build();
        Select timeStartSelect = Select.builder().column(Voucher_.TIME_START).build();
        Select timeExpiredSelect = Select.builder().column(Voucher_.TIME_EXPIRED).build();
        Select contentSelect = Select.builder().column(Voucher_.VOUCHER_CONTENT).build();
        Select costSelect = Select.builder().column(Voucher_.VOUCHER_COST).build();
        Select activeSelect = Select.builder().column(Voucher_.ACTIVE).build();
        Where idWhere = Where.builder().attribute(Voucher_.ID).value(id).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Voucher.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(discountPercentSelect)
                .select(timeStartSelect)
                .select(timeExpiredSelect)
                .select(contentSelect)
                .select(costSelect)
                .select(activeSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(query, attributes, getDomainClass());
    }

    @Override
    public boolean isVoucherNameExist(String voucherName) {
        var nameWhere = Where.builder().attribute(Voucher_.NAME).value(voucherName).operation(Operation.EQ).build();
        return getVoucher(Collections.emptyList(), Collections.emptyList(), Collections.singletonList(nameWhere)) != null;
    }

    @Override
    public boolean isVoucherIdExist(String voucherId) {
        return exists(voucherId);
    }

    @Override
    public void deleteVoucher(Voucher voucher) {
        delete(voucher);
    }

    @Override
    public void updateVouchers(Collection<Voucher> vouchers) {
        saveAll(vouchers);
    }

    @Override
    public void deleteVouchers(Collection<Voucher> vouchers) {
        this.deleteAll(vouchers);
    }

    @Override
    public Collection<Voucher> getListVoucher(Pageable pageable, Sort sort) {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        String sql;

        if (pageable == null && sort == null) {
            sql = sqlSelect.getNativeQuery();
            return getResultList(sql, Collections.emptyMap(), getDomainClass());
        }
        if (pageable != null && sort == null) {
            long offset = QueryUtils.calculateOffset(pageable.getPageNumber(), pageable.getPageSize());
            sqlSelect.limit(pageable.getPageSize()).offset(offset);
            sql = sqlSelect.getNativeQuery();
            return getResultList(sql, Collections.emptyMap(), getDomainClass());
        }
        if (pageable == null) {
            for (Sort.Order order : sort) {
                OrderBy orderBy = OrderBy.builder().column(order.getProperty()).type(order.isAscending() ? OrderBy.Type.ASC : OrderBy.Type.DESC).build();
                sqlSelect.orderBy(orderBy);
            }
            sql = sqlSelect.getNativeQuery();
            return getResultList(sql, Collections.emptyMap(), getDomainClass());
        }
        for (Sort.Order order : sort) {
            OrderBy orderBy = OrderBy.builder().column(order.getProperty()).type(order.isAscending() ? OrderBy.Type.ASC : OrderBy.Type.DESC).build();
            sqlSelect.orderBy(orderBy);
        }
        long offset = QueryUtils.calculateOffset(pageable.getPageNumber(), pageable.getPageSize());
        sqlSelect.limit(pageable.getPageSize()).offset(offset);
        sql = sqlSelect.getNativeQuery();
        return getResultList(sql, Collections.emptyMap(), getDomainClass());
    }

    @Override
    public Collection<Voucher> getVouchers(Collection<Select> selects, Collection<Join> joins, Collection<Where> wheres) {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Voucher.class);
        selects.forEach(sqlSelect::select);
        SqlJoin sqlJoin = null;
        if (!joins.isEmpty()) {
            for (var join : joins) {
                sqlJoin = sqlSelect.join(join);
            }
        }
        SqlWhere sqlWhere = null;
        if (!wheres.isEmpty()) {
            for (var where : wheres) {
                sqlWhere = Objects.requireNonNullElse(sqlJoin, sqlSelect).where(where);
            }
        }
        String query;
        Map<String, Object> attributes;
        if (sqlWhere == null) {
            query = sqlSelect.getNativeQuery();
            attributes = Collections.emptyMap();
        } else {
            query = sqlWhere.getNativeQuery();
            attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        }
        return this.getResultList(query, attributes, Voucher.class);
    }

    @Override
    public Collection<Voucher> getVouchersByIds(Collection<String> ids) {
        Where idsWhere = Where.builder().attribute(Voucher_.ID).value(ids).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Collection<Voucher> getVouchersAssignToCustomer(String customer, long page, long pageSize) {
        Join customerJoin = Join.builder().attribute(Voucher_.CUSTOMERS).type(JoinType.INNER).build();
        Where customerWhere = Where.builder().attribute(Customer_.ID).value(customer).operation(Operation.EQ).build();
        OrderBy timeStartOrderBy = OrderBy.builder().column(Voucher_.TIME_START).type(OrderBy.Type.ASC).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        SqlWhere sqlWhere = sqlJoin.where(customerWhere, Customer.class);
        sqlWhere.limit(page)
                .offset(QueryUtils.calculateOffset(page, pageSize))
                .orderBy(timeStartOrderBy);

        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(sql, attributes, getDomainClass());
    }

    @Override
    public Long countVoucherAssignToCustomer(String customerId) {
        Select idSelect = Select.builder().column(Voucher_.ID).build();
        Join customerJoin = Join.builder().attribute(Voucher_.CUSTOMERS).type(JoinType.INNER).build();
        Where customerWhere = Where.builder().attribute(Customer_.ID).value(customerId).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        sqlSelect.select(idSelect, getFunctionDialect().count());
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        SqlWhere sqlWhere = sqlJoin.where(customerWhere, Customer.class);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getSingleResult(query, attributes, Long.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new VoucherRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{VoucherRepository.class};
    }
}
