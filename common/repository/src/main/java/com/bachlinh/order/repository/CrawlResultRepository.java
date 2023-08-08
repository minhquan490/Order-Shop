package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.CrawlResult;

import java.time.LocalDateTime;
import java.util.Collection;

public interface CrawlResultRepository extends NativeQueryRepository {
    void saveCrawlResult(CrawlResult crawlResult);

    void deleteCrawlResult(CrawlResult crawlResult);

    void deleteCrawlResult(int id);

    void deleteCrawlResults(LocalDateTime localDateTime);

    Collection<CrawlResult> getCrawlResultToNow();
}
