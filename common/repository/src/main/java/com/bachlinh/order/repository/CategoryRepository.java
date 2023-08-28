package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Category;
import org.springframework.data.domain.Page;

import java.util.Collection;

public interface CategoryRepository extends NativeQueryRepository {

    Category getCategoryById(String categoryId);

    Category saveCategory(Category category);

    Category updateCategory(Category category);

    boolean deleteCategory(Category category);

    boolean isExits(String id);

    boolean isCategoryNameExisted(String name);

    Collection<Category> getCategoryByNames(Collection<String> names);

    Page<Category> getCategories();
}
