package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.DistrictRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Primary
public class DistrictRepositoryImpl extends AbstractRepository<District, Integer> implements DistrictRepository {

    @Autowired
    DistrictRepositoryImpl(ApplicationContext context) {
        super(District.class, context);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public boolean saveAllDistrict(Collection<District> districts) {
        return !saveAll(districts).isEmpty();
    }

    @Override
    public long countDistrict() {
        return count();
    }

    @Override
    public Collection<District> getAllDistrict() {
        return findAll();
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
