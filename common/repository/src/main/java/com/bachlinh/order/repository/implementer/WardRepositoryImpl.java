package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.WardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public class WardRepositoryImpl extends AbstractRepository<Ward, Integer> implements WardRepository {

    @Autowired
    WardRepositoryImpl(ApplicationContext context) {
        super(Ward.class, context);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public boolean saveAllWard(Collection<Ward> wards) {
        return !saveAll(wards).isEmpty();
    }

    @Override
    public long countAllWards() {
        return count();
    }

    @Override
    public Collection<Ward> getAllWards() {
        return findAll();
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
