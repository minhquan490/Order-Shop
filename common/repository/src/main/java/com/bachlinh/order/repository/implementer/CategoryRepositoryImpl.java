package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Category_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.JoinOperation;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelection;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.query.WhereOperation;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class CategoryRepositoryImpl extends AbstractRepository<Category, String> implements CategoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CategoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Category.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        var join = Join.builder().attribute(Category_.PRODUCTS).type(JoinType.INNER).build();
        var where = Where.builder().attribute(Category_.NAME).value(categoryName).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(Category.class);
        JoinOperation joinOperation = sqlSelection.join(join);
        return getCategory(joinOperation.where(where));
    }

    @Override
    public Category getCategoryById(String categoryId) {
        Where idWhere = Where.builder().attribute(Category_.ID).value(categoryId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(Category.class);
        return getCategory(sqlSelection.where(idWhere));
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
        return existsById(id);
    }

    @Override
    public Page<Category> getCategories() {
        Select idSelect = Select.builder().column(Category_.ID).build();
        Select nameSelect = Select.builder().column(Category_.NAME).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(Category.class);
        sqlSelection.select(idSelect).select(nameSelect);
        String query = sqlSelection.getNativeQuery();
        var results = executeNativeQuery(query, Collections.emptyMap(), Category.class);
        return new PageImpl<>(results);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private Category getCategory(WhereOperation whereOperation) {
        String query = whereOperation.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        whereOperation.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        var results = executeNativeQuery(query, attributes, Category.class);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}
