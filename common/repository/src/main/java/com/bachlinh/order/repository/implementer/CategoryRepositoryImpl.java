package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Category_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

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
        Specification<Category> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(Category_.PRODUCTS, JoinType.INNER);
            return criteriaBuilder.equal(root.get(Category_.NAME), categoryName);
        });
        return this.get(spec);
    }

    @Override
    public Category getCategoryById(String categoryId) {
        Specification<Category> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Category_.ID), categoryId));
        return this.get(spec);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Category saveCategory(Category category) {
        return this.save(category);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public Category updateCategory(Category category) {
        return saveCategory(category);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public boolean deleteCategory(Category category) {
        if (category == null) {
            return false;
        }
        if (existsById(category.getId())) {
            delete(category);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isExits(String id) {
        return existsById(id);
    }

    @Override
    public Page<Category> getCategories() {
        return this.findAll(Pageable.unpaged());
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
