package com.bachlinh.order.web.service.business;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.web.dto.form.common.ProductSearchForm;
import com.bachlinh.order.web.dto.resp.ProductResp;

public interface ProductSearchingService {

    Page<ProductResp> searchProduct(ProductSearchForm form, Pageable pageable);

    Page<ProductResp> fullTextSearch(ProductSearchForm form, Pageable pageable);
}
