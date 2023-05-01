package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProductMediaRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ActiveReflection
class ProductMediaRepositoryImpl extends AbstractRepository<ProductMedia, Integer> implements ProductMediaRepository {

    @Autowired
    ProductMediaRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(ProductMedia.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public ProductMedia loadMedia(int id) {
        return findById(id).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void saveMedia(ProductMedia productMedia) {
        save(productMedia);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
