package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProductMediaRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

@RepositoryComponent
@ActiveReflection
public class ProductMediaRepositoryImpl extends AbstractRepository<ProductMedia, Integer> implements ProductMediaRepository {

    @DependenciesInitialize
    @ActiveReflection
    public ProductMediaRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(ProductMedia.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public ProductMedia loadMedia(int id) {
        return findById(id).orElse(null);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
