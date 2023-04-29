package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProductMediaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Primary
class ProductMediaRepositoryImpl extends AbstractRepository<ProductMedia, Integer> implements ProductMediaRepository {

    @Autowired
    ProductMediaRepositoryImpl(ApplicationContext applicationContext) {
        super(ProductMedia.class, applicationContext);
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
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
