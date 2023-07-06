package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.resp.BatchReportResp;

import java.util.Collection;

public interface BatchReportService {
    Collection<BatchReportResp> getAllReport();

    Collection<BatchReportResp> getReportsInYear();

    Collection<BatchReportResp> getReportsInMonth();

    Collection<BatchReportResp> getReportsInWeek();

    Collection<BatchReportResp> getReportsInDay();
}
