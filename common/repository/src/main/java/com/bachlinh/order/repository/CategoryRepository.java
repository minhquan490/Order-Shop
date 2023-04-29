package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Category;
import org.springframework.data.domain.Page;

public interface CategoryRepository {

    Category getCategoryByName(String categoryName);

    Category getCategoryById(String categoryId);

    Category saveCategory(Category category);

    Category updateCategory(Category category);

    boolean deleteCategory(Category category);

    Page<Category> getCategories();
}
