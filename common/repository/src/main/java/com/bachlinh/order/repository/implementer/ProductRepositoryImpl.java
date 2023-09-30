package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Category_;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.entity.model.ProductMedia_;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.ProductRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class ProductRepositoryImpl extends AbstractRepository<String, Product> implements ProductRepository, RepositoryBase {

    private ProductRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Product.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Product saveProduct(Product product) {
        return this.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return this.saveProduct(product);
    }

    @Override
    public boolean deleteProduct(Product product) {
        if (product == null) {
            return false;
        }
        delete(product);
        return true;
    }

    @Override
    public boolean productNameExist(Product product) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Where nameWhere = Where.builder().attribute(Product_.NAME).value(product.getName()).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class).select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(nameWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = this.getResultList(sql, attributes, Product.class);
        return !results.isEmpty();
    }

    @Override
    public boolean isProductExist(String productId) {
        return exists(productId);
    }

    @Override
    public long countProduct() {
        return count();
    }

    @Override
    public Product getProductForFileUpload(String productId) {
        return getProduct(productId);
    }

    @Override
    public Product getProductForUpdate(String productId) {
        Where idWhere = Where.builder().attribute(Product_.ID).value(productId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        return getProduct(sqlSelect.where(idWhere));
    }

    @Override
    public Product getProductForDelete(String productId) {
        return getProduct(productId);
    }

    @Override
    public Product getProductInfo(String productId) {
        var results = getProductInfos(Collections.singleton(productId));
        if (results.isEmpty()) {
            return null;
        } else {
            return new ArrayList<>(results).get(0);
        }
    }

    @Override
    public Collection<Product> getProductInfos(Collection<String> productIds) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Select nameSelect = Select.builder().column(Product_.NAME).build();
        Select priceSelect = Select.builder().column(Product_.PRICE).build();
        Select sizeSelect = Select.builder().column(Product_.SIZE).build();
        Select colorSelect = Select.builder().column(Product_.COLOR).build();
        Select taobaoUrlSelect = Select.builder().column(Product_.TAOBAO_URL).build();
        Select descriptionSelect = Select.builder().column(Product_.DESCRIPTION).build();
        Select orderPointSelect = Select.builder().column(Product_.ORDER_POINT).build();
        Select enableSelect = Select.builder().column(Product_.ENABLED).build();
        Select productMediaIdSelect = Select.builder().column(ProductMedia_.ID).build();
        Select categoryNameSelect = Select.builder().column(Category_.NAME).build();
        Join productMediasJoin = Join.builder().attribute(Product_.MEDIAS).type(JoinType.INNER).build();
        Join categoriesJoin = Join.builder().attribute(Product_.CATEGORIES).type(JoinType.LEFT).build();
        Where productIdWhere = Where.builder().attribute(Product_.ID).value(productIds).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(priceSelect)
                .select(sizeSelect)
                .select(colorSelect)
                .select(taobaoUrlSelect)
                .select(descriptionSelect)
                .select(orderPointSelect)
                .select(enableSelect)
                .select(productMediaIdSelect, ProductMedia.class)
                .select(categoryNameSelect, Category.class);
        SqlJoin sqlJoin = sqlSelect.join(productMediasJoin).join(categoriesJoin);
        SqlWhere sqlWhere = sqlJoin.where(productIdWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, Product.class);
    }

    @Override
    public Collection<Product> getProductsForSavingOrder(Collection<String> productIds) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Where idsWhere = Where.builder().attribute(Product_.ID).value(productIds).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, Product.class);
    }

    @Override
    @Deprecated(forRemoval = true)
    public Page<Product> getProductsByCondition(Map<String, Object> conditions, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    @Deprecated(forRemoval = true)
    public Page<Product> getProductsWithUnion(Collection<String> ids, Map<String, Object> conditions, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        OrderBy idOrderBy = OrderBy.builder().column(Product_.ID).type(OrderBy.Type.ASC).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        sqlSelect.orderBy(idOrderBy);
        long offset = QueryUtils.calculateOffset(pageable.getPageNumber(), pageable.getPageSize());
        sqlSelect.offset(offset).limit(pageable.getPageSize());
        var results = getResultList(sqlSelect.getNativeQuery(), Collections.emptyMap(), getDomainClass());
        return new PageImpl<>(results);
    }

    @Override
    public Collection<Product> getProducts(Pageable pageable) {
        var mediasJoin = Join.builder().attribute(Product_.MEDIAS).type(JoinType.LEFT).build();
        var categoriesJoin = Join.builder().attribute(Product_.CATEGORIES).type(JoinType.INNER).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        SqlJoin sqlJoin = sqlSelect.join(mediasJoin).join(categoriesJoin);
        sqlJoin.offset(pageable.getOffset());
        sqlJoin.limit(pageable.getPageSize());
        String sql = sqlJoin.getNativeQuery();
        return this.getResultList(sql, Collections.emptyMap(), Product.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new ProductRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{ProductRepository.class};
    }

    @Nullable
    private Product getProduct(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, Product.class);
    }

    @Nullable
    private Product getProduct(String productId) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Where idWhere = Where.builder().attribute(Product_.ID).value(productId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        sqlSelect.select(idSelect);
        return getProduct(sqlSelect.where(idWhere));
    }
}