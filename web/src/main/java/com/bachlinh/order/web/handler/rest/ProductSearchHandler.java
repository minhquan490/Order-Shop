package com.bachlinh.order.web.handler.rest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.ProductSearchForm;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.business.ProductSearchingService;

@ActiveReflection
@RouteProvider
public class ProductSearchHandler extends AbstractController<ResponseEntity<Page<ProductResp>>, ProductSearchForm> {
    private String productSearchUrl;
    private ProductSearchingService searchingService;

    @ActiveReflection
    public ProductSearchHandler() {
    }

    @Override
    protected ResponseEntity<Page<ProductResp>> internalHandler(Payload<ProductSearchForm> request) {
        return search(request.data());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (searchingService == null) {
            searchingService = resolver.resolveDependencies(ProductSearchingService.class);
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

    private ResponseEntity<Page<ProductResp>> search(ProductSearchForm form) {
        int page;
        int pageSize;

        try {
            page = Integer.parseInt(form.page());
            pageSize = Integer.parseInt(form.size());
        } catch (NumberFormatException e) {
            throw new BadVariableException("Page number and page size must be int");
        }

        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<ProductResp> results;
        if (form.mode().equals("full")) {
            results = searchingService.fullTextSearch(form, pageRequest);
        } else {
            results = searchingService.searchProduct(form, pageRequest);
        }
        return ResponseEntity.ok(results);
    }
}
