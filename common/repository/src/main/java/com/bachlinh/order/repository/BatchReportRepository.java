package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.BatchReport;

import java.sql.Timestamp;
import java.util.Collection;

public interface BatchReportRepository {
    void saveAllReport(Collection<BatchReport> reports);

    void deleteReport(Collection<Integer> reportIds);

    Collection<BatchReport> getReports(Timestamp from, Timestamp to);
}
