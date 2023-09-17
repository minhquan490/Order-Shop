package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.CrawlResult;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CrawlResultRepository extends NativeQueryRepository {
    void saveCrawlResult(CrawlResult crawlResult);

    void deleteCrawlResults(LocalDateTime localDateTime);

    Collection<CrawlResult> getCrawlResultToNow();
}
