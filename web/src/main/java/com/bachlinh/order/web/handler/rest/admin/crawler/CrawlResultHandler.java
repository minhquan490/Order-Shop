package com.bachlinh.order.web.handler.rest.admin.crawler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CrawlResultResp;
import com.bachlinh.order.web.service.business.CrawlerResultService;
import lombok.NoArgsConstructor;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "crawlResultHandler")
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Permit(roles = Role.ADMIN)
public class CrawlResultHandler extends AbstractController<Collection<CrawlResultResp>, Object> {
    private CrawlerResultService crawlerResultService;
    private String path;

    @Override
    @ActiveReflection
    protected Collection<CrawlResultResp> internalHandler(Payload<Object> request) {
        return crawlerResultService.getCrawlerResultInDate()
                .stream()
                .map(CrawlResultResp::new)
                .toList();
    }

    @Override
    protected void inject() {
        DependenciesResolver dependenciesResolver = getContainerResolver().getDependenciesResolver();
        if (crawlerResultService == null) {
            crawlerResultService = dependenciesResolver.resolveDependencies(CrawlerResultService.class);
        }
    }

    @Override
    public String getPath() {
        if (path == null) {
            path = getEnvironment().getProperty("shop.url.admin.crawl.list");
        }
        return path;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
