package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Map;

public interface ProductRepository extends NativeQueryRepository {

    Product saveProduct(Product product);

    Product updateProduct(Product product);

    boolean deleteProduct(Product product);

    boolean productNameExist(Product product);

    boolean isProductExist(String productId);

    long countProduct();

    Product getProductForFileUpload(String productId);

    Product getProductForUpdate(String productId);

    Product getProductForDelete(String productId);

    Product getProductInfo(String productId);

    Collection<Product> getProductInfos(Collection<String> productIds);

    Collection<Product> getProductsForSavingOrder(Collection<String> productIds);

    @Deprecated(forRemoval = true)
    Page<Product> getProductsByCondition(Map<String, Object> conditions, Pageable pageable);

    @Deprecated(forRemoval = true)
    Page<Product> getProductsWithUnion(Collection<String> ids, Map<String, Object> conditions, Pageable pageable);

    Page<Product> getAllProducts(Pageable pageable);

    Collection<Product> getProducts(Pageable pageable);
}
