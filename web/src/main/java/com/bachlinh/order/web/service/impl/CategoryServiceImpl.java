package com.bachlinh.order.web.service.impl;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.web.dto.form.CategoryDeleteForm;
import com.bachlinh.order.web.dto.form.admin.CategoryCreateForm;
import com.bachlinh.order.web.dto.form.admin.CategoryUpdateForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;

import java.util.Collection;

@ServiceComponent
@ActiveReflection
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityFactory entityFactory;

    @DependenciesInitialize
    @ActiveReflection
    public CategoryServiceImpl(CategoryRepository categoryRepository, EntityFactory entityFactory) {
        this.categoryRepository = categoryRepository;
        this.entityFactory = entityFactory;
    }

    @Override
    public boolean isExist(String id) {
        return categoryRepository.isExits(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CategoryResp saveCategory(CategoryCreateForm form) {
        var category = entityFactory.getEntity(Category.class);
        category.setName(form.name());
        category = categoryRepository.saveCategory(category);
        return new CategoryResp(category.getId(), category.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CategoryResp updateCategory(CategoryUpdateForm form) {
        var category = categoryRepository.getCategoryById(form.id());
        category.setName(form.name());
        category = categoryRepository.saveCategory(category);
        return new CategoryResp(category.getId(), category.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public boolean deleteCategory(CategoryDeleteForm form) {
        var category = categoryRepository.getCategoryById(form.id());
        if (category == null) {
            return false;
        } else {
            return categoryRepository.deleteCategory(category);
        }
    }

    @Override
    public Collection<CategoryResp> getCategories() {
        return categoryRepository.getCategories()
                .stream()
                .map(category -> new CategoryResp(category.getId(), category.getName()))
                .toList();
    }
}
