package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.entity.model.ProductMedia_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.ProductMediaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class ProductMediaRepositoryImpl extends AbstractRepository<Integer, ProductMedia> implements ProductMediaRepository, RepositoryBase {

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
    public void saveMedia(ProductMedia productMedia) {
        save(productMedia);
    }

    @Override
    public void deleteMedia(String id) {
        try {
            int mediaId = Integer.parseInt(id);
            deleteById(mediaId);
        } catch (NumberFormatException e) {
            log.warn("Id [{}] is not a valid id", id);
        }
    }

    @Override
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
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new ProductMediaRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{ProductMediaRepository.class};
    }
}
