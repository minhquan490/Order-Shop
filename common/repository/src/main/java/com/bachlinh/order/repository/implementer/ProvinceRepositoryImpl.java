package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@ActiveReflection
class ProvinceRepositoryImpl extends AbstractRepository<Province, Integer> implements ProvinceRepository {

    @Autowired
    @ActiveReflection
    ProvinceRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Province.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean saveAllProvinces(Collection<Province> provinces) {
        return !saveAll(provinces).isEmpty();
    }

    @Override
    public long countProvince() {
        return count();
    }

    @Override
    public Collection<Province> getAllProvinces() {
        return findAll();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void remove(Province province) {
        deleteById(province.getId());
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
