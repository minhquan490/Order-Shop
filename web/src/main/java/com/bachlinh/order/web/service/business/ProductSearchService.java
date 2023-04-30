package com.bachlinh.order.web.service.business;

import com.bachlinh.order.web.dto.form.ProductSearchForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSearchService {
    Page<ProductResp> searchProduct(ProductSearchForm form, Pageable pageable);
}
