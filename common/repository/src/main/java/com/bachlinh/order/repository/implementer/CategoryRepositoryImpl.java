package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Category_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class CategoryRepositoryImpl extends AbstractRepository<String, Category> implements CategoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CategoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Category.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Category getCategoryById(String categoryId) {
        Where idWhere = Where.builder().attribute(Category_.ID).value(categoryId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Category.class);
        return getCategory(sqlSelect.where(idWhere));
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Category saveCategory(Category category) {
        return this.save(category);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Category updateCategory(Category category) {
        return saveCategory(category);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteCategory(Category category) {
        if (category == null) {
            return false;
        }
        delete(category);
        return true;
    }

    @Override
    public boolean isExits(String id) {
        return exists(id);
    }

    @Override
    public boolean isCategoryNameExisted(String name) {
        Select idSelect = Select.builder().column(Category_.ID).build();
        Where nameWhere = Where.builder().attribute(Category_.NAME).value(name).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Category.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(nameWhere);
        return getCategory(sqlWhere) != null;
    }

    @Override
    public Collection<Category> getCategoryByNames(Collection<String> names) {
        Select idSelect = Select.builder().column(Category_.ID).build();
        Select nameSelect = Select.builder().column(Category_.NAME).build();
        Where namesWhere = Where.builder().attribute(Category_.NAME).value(names).operator(Operator.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Category.class);
        sqlSelect.select(idSelect).select(nameSelect);
        SqlWhere sqlWhere = sqlSelect.where(namesWhere);
        String query = sqlSelect.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(query, attributes, Category.class);
    }

    @Override
    public Page<Category> getCategories() {
        Select idSelect = Select.builder().column(Category_.ID).build();
        Select nameSelect = Select.builder().column(Category_.NAME).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Category.class);
        sqlSelect.select(idSelect).select(nameSelect);
        String query = sqlSelect.getNativeQuery();
        var results = this.getResultList(query, Collections.emptyMap(), Category.class);
        return new PageImpl<>(results);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private Category getCategory(SqlWhere sqlWhere) {
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        sqlWhere.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        return getSingleResult(query, attributes, Category.class);
    }
}
