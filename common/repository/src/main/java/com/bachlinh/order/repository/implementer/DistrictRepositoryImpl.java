package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.DistrictRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class DistrictRepositoryImpl extends AbstractRepository<District, Integer> implements DistrictRepository {

    @DependenciesInitialize
    @ActiveReflection
    public DistrictRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(District.class, containerResolver.getDependenciesResolver());
    }

    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
    public Collection<District> getDistricts(Collection<String> ids) {
        return findAllById(ids.stream().map(Integer::parseInt).toList());
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
