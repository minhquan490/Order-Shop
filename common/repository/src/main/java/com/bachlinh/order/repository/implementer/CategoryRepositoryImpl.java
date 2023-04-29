package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.entity.model.Category_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Primary
class CategoryRepositoryImpl extends AbstractRepository<Category, String> implements CategoryRepository {

    @Autowired
    CategoryRepositoryImpl(ApplicationContext applicationContext) {
        super(Category.class, applicationContext);
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
    public Page<Category> getCategories() {
        return this.findAll(Pageable.unpaged());
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
