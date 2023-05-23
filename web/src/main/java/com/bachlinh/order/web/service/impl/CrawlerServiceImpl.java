package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.model.CrawlResult;
import com.bachlinh.order.repository.CrawlResultRepository;
import com.bachlinh.order.web.service.business.CrawlerResultService;

import java.util.LinkedList;
import java.util.List;

@ServiceComponent
@ActiveReflection
public class CrawlerServiceImpl implements CrawlerResultService {
    private final CrawlResultRepository crawlResultRepository;

    @ActiveReflection
    @DependenciesInitialize
    public CrawlerServiceImpl(CrawlResultRepository crawlResultRepository) {
        this.crawlResultRepository = crawlResultRepository;
    }

    @Override
    public List<CrawlResult> getCrawlerResultInDate() {
        return new LinkedList<>(crawlResultRepository.getCrawlResultToNow());
    }
}
