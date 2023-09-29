package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.CrawlResult;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.CrawlResultRepository;
import com.bachlinh.order.web.service.business.CrawlerResultService;

import java.util.LinkedList;
import java.util.List;

@ServiceComponent
public class CrawlerServiceImpl extends AbstractService implements CrawlerResultService {
    private final CrawlResultRepository crawlResultRepository;

    private CrawlerServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.crawlResultRepository = resolveRepository(CrawlResultRepository.class);
    }

    @Override
    public List<CrawlResult> getCrawlerResultInDate() {
        return new LinkedList<>(crawlResultRepository.getCrawlResultToNow());
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new CrawlerServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{CrawlerResultService.class};
    }
}
