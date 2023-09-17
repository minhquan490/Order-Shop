package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.entity.model.ProductMedia_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.ProductMediaRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class ProductMediaRepositoryImpl extends AbstractRepository<Integer, ProductMedia> implements ProductMediaRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @DependenciesInitialize
    @ActiveReflection
    public ProductMediaRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(ProductMedia.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public ProductMedia loadMedia(int id) {
        Where idWhere = Where.builder().attribute(ProductMedia_.ID).value(id).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(query, attributes, getDomainClass());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void saveMedia(ProductMedia productMedia) {
        save(productMedia);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteMedia(String id) {
        try {
            int mediaId = Integer.parseInt(id);
            deleteById(mediaId);
        } catch (NumberFormatException e) {
            log.warn("Id [{}] is not a valid id", id);
        }
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = MANDATORY)
    public void deleteMedia(Product product) {
        Where productWhere = Where.builder().attribute(ProductMedia_.PRODUCT).value(product).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(productWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        ProductMedia productMedia = getSingleResult(query, attributes, getDomainClass());

        delete(productMedia);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
