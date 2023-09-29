package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Category;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.CategoryRepository;
import com.bachlinh.order.web.dto.form.admin.category.CategoryCreateForm;
import com.bachlinh.order.web.dto.form.admin.category.CategoryDeleteForm;
import com.bachlinh.order.web.dto.form.admin.category.CategoryUpdateForm;
import com.bachlinh.order.web.dto.resp.CategoryResp;
import com.bachlinh.order.web.service.common.CategoryService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@ServiceComponent
public class CategoryServiceImpl extends AbstractService implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    private CategoryServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.categoryRepository = resolveRepository(CategoryRepository.class);
        this.entityFactory = getResolver().resolveDependencies(EntityFactory.class);
        this.dtoMapper = getResolver().resolveDependencies(DtoMapper.class);
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
        return dtoMapper.map(category, CategoryResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CategoryResp updateCategory(CategoryUpdateForm form) {
        var category = categoryRepository.getCategoryById(form.id());
        category.setName(form.name());
        category = categoryRepository.updateCategory(category);
        return dtoMapper.map(category, CategoryResp.class);
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
                .map(category -> dtoMapper.map(category, CategoryResp.class))
                .toList();
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new CategoryServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[0];
    }
}
