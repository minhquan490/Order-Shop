package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.sql.Timestamp;
import java.util.Collection;

public interface BatchReportRepository extends NativeQueryRepository {
    void saveAllReport(Collection<BatchReport> reports);

    void deleteReport(Collection<Integer> reportIds);

    Collection<BatchReport> getReports(Timestamp from, Timestamp to);

    Collection<BatchReport> getAllReport();
}
