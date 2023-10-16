package com.bachlinh.order.web.handler.rest.customer.product;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.core.utils.ValidateUtils;
import com.bachlinh.order.web.dto.form.common.ProductSearchForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.business.ProductSearchingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "productSearchHandler")
public class ProductSearchHandler extends AbstractController<Collection<ProductResp>, ProductSearchForm> {
    private String productSearchUrl;
    private ProductSearchingService searchingService;
    private Integer defaultPageSize;

    private ProductSearchHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<ProductResp>, ProductSearchForm> newInstance() {
        return new ProductSearchHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<ProductResp> internalHandler(Payload<ProductSearchForm> request) {
        return search(request.data());
    }

    @Override
    protected void inject() {
        if (searchingService == null) {
            searchingService = resolveService(ProductSearchingService.class);
        }
        if (defaultPageSize == null) {
            defaultPageSize = Integer.parseInt(getEnvironment().getProperty("data.default.page.size"));
        }
    }

    @Override
    public String getPath() {
        if (productSearchUrl == null) {
            productSearchUrl = getEnvironment().getProperty("shop.url.content.product.search");
        }
        return productSearchUrl;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }

    private Collection<ProductResp> search(ProductSearchForm form) {
        int page;
        int pageSize;
        if (ValidateUtils.isNumber(form.page())) {
            page = Integer.parseInt(form.page());
        } else {
            page = 1;
        }
        if (ValidateUtils.isNumber(form.size())) {
            pageSize = Integer.parseInt(form.size());
        } else {
            pageSize = defaultPageSize;
        }
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<ProductResp> results;
        if (form.mode().equals("full")) {
            results = searchingService.fullTextSearch(form, pageRequest);
        } else {
            results = searchingService.searchProduct(form, pageRequest);
        }
        return results.toList();
    }
}
