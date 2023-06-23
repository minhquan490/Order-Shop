package com.bachlinh.order.repository;

import org.springframework.data.domain.Page;
import com.bachlinh.order.entity.model.Category;

public interface CategoryRepository {

    Category getCategoryByName(String categoryName);

    Category getCategoryById(String categoryId);

    Category saveCategory(Category category);

    Category updateCategory(Category category);

    boolean deleteCategory(Category category);

    boolean isExits(String id);

    Page<Category> getCategories();
}
