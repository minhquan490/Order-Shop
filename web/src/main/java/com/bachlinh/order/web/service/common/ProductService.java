package com.bachlinh.order.web.service.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.web.dto.form.admin.product.ProductCreateForm;
import com.bachlinh.order.web.dto.form.admin.product.ProductUpdateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;

import java.util.Collection;

public interface ProductService {
    Page<ProductResp> productList(Pageable pageable);

    Page<ProductResp> getProductsWithId(Collection<Object> ids);

    ProductResp updateProduct(ProductUpdateForm form);

    ProductResp createProduct(ProductCreateForm form);

    ProductResp getProductById(String productId);

    boolean deleteProduct(String productId);
}
