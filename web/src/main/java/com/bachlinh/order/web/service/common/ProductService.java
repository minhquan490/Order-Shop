package com.bachlinh.order.web.service.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.service.BaseService;
import com.bachlinh.order.web.dto.form.ProductForm;
import com.bachlinh.order.web.dto.form.admin.ProductCreateForm;
import com.bachlinh.order.web.dto.form.admin.ProductUpdateForm;
import com.bachlinh.order.web.dto.resp.ProductResp;

import java.util.Collection;

public interface ProductService extends BaseService<ProductResp, ProductForm> {
    Page<ProductResp> productList(Pageable pageable);

    Page<ProductResp> getProductsWithId(Collection<Object> ids);

    ProductResp updateProduct(ProductUpdateForm form);

    ProductResp createProduct(ProductCreateForm form);
}
