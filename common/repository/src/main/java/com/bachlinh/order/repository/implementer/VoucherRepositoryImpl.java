package com.bachlinh.order.repository.implementer;


import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.VoucherRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class VoucherRepositoryImpl extends AbstractRepository<Voucher, String> implements VoucherRepository {

    @Autowired
    VoucherRepositoryImpl(ApplicationContext applicationContext) {
        super(Voucher.class, applicationContext);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Voucher saveVoucher(Voucher voucher) {
        return save(voucher);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public Voucher updateVoucher(Voucher voucher) {
        return save(voucher);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void deleteVoucher(Voucher voucher) {
        delete(voucher);
    }

    @Override
    public Page<Voucher> getListVoucher(Pageable pageable, Sort sort) {
        if (pageable == null && sort == null) {
            List<Voucher> results = this.findAll();
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        if (pageable != null && sort == null) {
            return this.findAll(pageable);
        }
        if (pageable == null) {
            List<Voucher> results = this.findAll(sort);
            return new PageImpl<>(results, Pageable.unpaged(), results.size());
        }
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.findAll(newPageable);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
