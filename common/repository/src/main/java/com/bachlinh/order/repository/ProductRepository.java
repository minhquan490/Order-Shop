package com.bachlinh.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.entity.model.Product;

import java.util.Collection;
import java.util.Map;

public interface ProductRepository extends NativeQueryRepository {

    Product saveProduct(Product product);

    Product updateProduct(Product product);

    boolean deleteProduct(Product product);

    boolean productNameExist(Product product);

    boolean isProductExist(String productId);

    long countProduct();

    Product getProductByCondition(Map<String, Object> conditions);

    Page<Product> getProductsByCondition(Map<String, Object> conditions, Pageable pageable);

    Page<Product> getProductsWithUnion(Collection<String> ids, Map<String, Object> conditions, Pageable pageable);

    Page<Product> getAllProducts(Pageable pageable);
}
