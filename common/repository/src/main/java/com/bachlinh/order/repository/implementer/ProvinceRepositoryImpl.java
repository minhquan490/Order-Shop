package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class ProvinceRepositoryImpl extends AbstractRepository<Province, Integer> implements ProvinceRepository {

    @DependenciesInitialize
    @ActiveReflection
    public ProvinceRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Province.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
    public Collection<Province> getProvincesById(Collection<String> ids) {
        return findAllById(ids.stream().map(Integer::parseInt).toList());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void remove(Province province) {
        deleteById(province.getId());
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
