package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Map;

public interface ProductRepository {

    Product saveProduct(Product product);

    Product updateProduct(Product product);

    boolean deleteProduct(Product product);

    boolean productNameExist(Product product);

    long countProduct();

    Product getProductByCondition(Map<String, Object> conditions);

    Page<Product> getProductsByCondition(Map<String, Object> conditions, Pageable pageable);

    Page<Product> getProductsWithUnion(Collection<String> ids, Map<String, Object> conditions, Pageable pageable);

    Collection<Product> getAllProducts();
}
