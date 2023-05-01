package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.WardRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@ActiveReflection
public class WardRepositoryImpl extends AbstractRepository<Ward, Integer> implements WardRepository {

    @Autowired
    @ActiveReflection
    WardRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Ward.class, containerResolver.getDependenciesResolver());
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
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
