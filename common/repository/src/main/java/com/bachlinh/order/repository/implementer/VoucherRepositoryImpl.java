package com.bachlinh.order.repository.implementer;


import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class VoucherRepositoryImpl extends AbstractRepository<Voucher, String> implements VoucherRepository {

    @DependenciesInitialize
    @ActiveReflection
    public VoucherRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Voucher.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Voucher saveVoucher(Voucher voucher) {
        return save(voucher);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
        var idWhere = Where.builder().attribute(Voucher_.ID).value(id).operator(Operator.EQ).build();
        return getVoucher(selects, Collections.emptyList(), Collections.singletonList(idWhere));
    }

    @Override
    public boolean isVoucherNameExist(String voucherName) {
        var nameWhere = Where.builder().attribute(Voucher_.NAME).value(voucherName).operator(Operator.EQ).build();
        return getVoucher(Collections.emptyList(), Collections.emptyList(), Collections.singletonList(nameWhere)) != null;
    }

    @Override
    public boolean isVoucherIdExist(String voucherId) {
        return existsById(voucherId);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteVoucher(Voucher voucher) {
        delete(voucher);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void updateVouchers(Collection<Voucher> vouchers) {
        saveAll(vouchers);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteVouchers(Collection<Voucher> vouchers) {
        this.deleteAll(vouchers);
    }

    @Override
    public Collection<Voucher> getListVoucher(Pageable pageable, Sort sort) {
        if (pageable == null && sort == null) {
            return this.findAll();
        }
        if (pageable != null && sort == null) {
            return this.findAll(pageable).toList();
        }
        if (pageable == null) {
            return this.findAll(sort);
        }
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.findAll(newPageable).toList();
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
        return executeNativeQuery(query, attributes, Voucher.class);
    }

    @Override
    public Collection<Voucher> getVouchersByIds(Collection<String> ids) {
        return findAllById(ids);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
