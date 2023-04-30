package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.service.AbstractService;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.CategoryForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
class CategoryServiceImpl extends AbstractService<CategoryResp, CategoryForm> implements CategoryService {
    private CategoryRepository categoryRepository;
    private EntityFactory entityFactory;

    @Autowired
    CategoryServiceImpl(ThreadPoolTaskExecutor executor, ContainerWrapper wrapper, String profile) {
        super(executor, wrapper, profile);
    }

    @Override
    protected CategoryResp doSave(CategoryForm param) {
        Category category = entityFactory.getEntity(Category.class);
        category.setName(param.getName());
        category = categoryRepository.saveCategory(category);
        return new CategoryResp(category.getId(), category.getName());
    }

    @Override
    protected CategoryResp doUpdate(CategoryForm param) {
        Category category = categoryRepository.getCategoryById(param.getId());
        category.setName(param.getName());
        category = categoryRepository.saveCategory(category);
        return new CategoryResp(category.getId(), category.getName());
    }

    @Override
    protected CategoryResp doDelete(CategoryForm param) {
        Category category = categoryRepository.getCategoryById(param.getName());
        if (categoryRepository.deleteCategory(category)) {
            return new CategoryResp(category.getId(), category.getName());
        } else {
            return new CategoryResp(null, null);
        }
    }

    @Override
    protected CategoryResp doGetOne(CategoryForm param) {
        if (param.getName() == null && param.getId() != null) {
            Category category = categoryRepository.getCategoryById(param.getId());
            return new CategoryResp(category.getId(), category.getName());
        }
        if (param.getName() != null && param.getId() == null) {
            Category category = categoryRepository.getCategoryByName(param.getName());
            return new CategoryResp(category.getId(), category.getName());
        }
        if (param.getName() != null && param.getId() != null) {
            Category category = categoryRepository.getCategoryById(param.getId());
            return new CategoryResp(category.getId(), category.getName());
        }
        return new CategoryResp(null, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K, X extends Iterable<K>> X doGetList(CategoryForm param) {
        Page<CategoryResp> categories = categoryRepository.getCategories().map(category -> new CategoryResp(category.getId(), category.getName()));
        return (X) categories;
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (categoryRepository == null) {
            categoryRepository = resolver.resolveDependencies(CategoryRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolver.resolveDependencies(EntityFactory.class);
        }
    }
}
