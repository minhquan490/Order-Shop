package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Category_;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.ProductMedia;
import com.bachlinh.order.entity.model.ProductMedia_;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class ProductRepositoryImpl extends AbstractRepository<Product, String> implements ProductRepository {
    private static final String LIKE_PATTERN = "%{0}%";

    @DependenciesInitialize
    @ActiveReflection
    public ProductRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Product.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Product saveProduct(Product product) {
        return this.save(product);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
    public Product updateProduct(Product product) {
        return this.saveProduct(product);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
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
        Where nameWhere = Where.builder().attribute(Product_.NAME).value(product.getName()).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class).select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(nameWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, Product.class);
        return !results.isEmpty();
    }

    @Override
    public boolean isProductExist(String productId) {
        return existsById(productId);
    }

    @Override
    public long countProduct() {
        return count();
    }

    @Override
    public Product getProductForFileUpload(String productId) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Where idWhere = Where.builder().attribute(Product_.ID).value(productId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        sqlSelect.select(idSelect);
        return getProduct(sqlSelect.where(idWhere));
    }

    @Override
    public Product getProductForUpdate(String productId) {
        Where idWhere = Where.builder().attribute(Product_.ID).value(productId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        return getProduct(sqlSelect.where(idWhere));
    }

    @Override
    public Product getProductForDelete(String productId) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Where idWhere = Where.builder().attribute(Product_.ID).value(productId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        sqlSelect.select(idSelect);
        return getProduct(sqlSelect.where(idWhere));
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
        Where productIdWhere = Where.builder().attribute(Product_.ID).value(productIds).operator(Operator.IN).build();
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
        return executeNativeQuery(sql, attributes, Product.class);
    }

    @Override
    public Collection<Product> getProductsForSavingOrder(Collection<String> productIds) {
        Select idSelect = Select.builder().column(Product_.ID).build();
        Where idsWhere = Where.builder().attribute(Product_.ID).value(productIds).operator(Operator.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Product.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return executeNativeQuery(sql, attributes, Product.class);
    }

    @Override
    @Deprecated(forRemoval = true)
    public Page<Product> getProductsByCondition(Map<String, Object> conditions, Pageable pageable) {
        Specification<Product> spec = specWithCondition(conditions);
        return findAll(spec, pageable);
    }

    @Override
    @Deprecated(forRemoval = true)
    public Page<Product> getProductsWithUnion(Collection<String> ids, Map<String, Object> conditions, Pageable pageable) {
        Specification<Product> spec = specWithCondition(conditions);
        return unionQueryWithId(ids, spec, pageable);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return findAll(pageable);
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
        return executeNativeQuery(sql, Collections.emptyMap(), Product.class);
    }

    private Specification<Product> specWithCondition(Map<String, Object> conditions) {
        Map<String, Object> copyConditions = conditions.entrySet()
                .stream()
                .filter(stringObjectEntry -> stringObjectEntry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Specification.where(((root, query, criteriaBuilder) -> {
            query.multiselect(
                    root.get(Product_.id),
                    root.get(Product_.carts),
                    root.get(Product_.size),
                    root.get(Product_.name),
                    root.get(Product_.color),
                    root.get(Product_.price),
                    root.get(Product_.taobaoUrl),
                    root.get(Product_.description),
                    root.get(Product_.categories),
                    root.get(Product_.medias)
            );
            root.join(Product_.categories, JoinType.INNER);
            root.join(Product_.medias, JoinType.LEFT);
            AtomicReference<Predicate> predicateWrapper = new AtomicReference<>();
            copyConditions.forEach((key, value) -> {
                Predicate predicate = switch (key) {
                    case Product_.PRICE -> criteriaBuilder.lessThanOrEqualTo(root.get(key), (int) value);
                    case Product_.NAME ->
                            criteriaBuilder.like(root.get(Product_.NAME), MessageFormat.format(LIKE_PATTERN, value));
                    case "IDS" -> criteriaBuilder.in(root.get(Product_.ID)).in(value);
                    case Product_.CATEGORIES -> criteriaBuilder.in(root.get(Product_.CATEGORIES)).in(value);
                    default -> criteriaBuilder.equal(root.get(key), value);
                };
                predicateWrapper.set(criteriaBuilder.and(predicate));
            });
            return predicateWrapper.get();
        }));
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private Product getProduct(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, Product.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
}