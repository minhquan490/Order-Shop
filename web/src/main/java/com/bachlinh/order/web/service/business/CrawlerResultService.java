package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.CrawlResult;

import java.util.List;

public interface CrawlerResultService {
    List<CrawlResult> getCrawlerResultInDate();
}
