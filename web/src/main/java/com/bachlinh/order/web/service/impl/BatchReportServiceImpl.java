package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.repository.BatchReportRepository;
import com.bachlinh.order.web.dto.resp.BatchReportResp;
import com.bachlinh.order.web.service.common.BatchReportService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__(@ActiveReflection))
public class BatchReportServiceImpl implements BatchReportService {
    private final BatchReportRepository batchReportRepository;
    private final DtoMapper dtoMapper;

    @Override
    public Collection<BatchReportResp> getAllReport() {
        var reports = batchReportRepository.getAllReport();
        return dtoMapper.map(reports, BatchReportResp.class);
    }

    @Override
    public Collection<BatchReportResp> getReportsInYear() {
        var from = Timestamp.valueOf(LocalDateTime.now().plusYears(-1));
        var now = Timestamp.from(Instant.now());
        var reports = batchReportRepository.getReports(from, now);
        return dtoMapper.map(reports, BatchReportResp.class);
    }

    @Override
    public Collection<BatchReportResp> getReportsInMonth() {
        var from = Timestamp.valueOf(LocalDateTime.now().plusMonths(-1));
        var now = Timestamp.from(Instant.now());
        var reports = batchReportRepository.getReports(from, now);
        return dtoMapper.map(reports, BatchReportResp.class);
    }

    @Override
    public Collection<BatchReportResp> getReportsInWeek() {
        var from = Timestamp.valueOf(LocalDateTime.now().plusWeeks(-1));
        var now = Timestamp.from(Instant.now());
        var reports = batchReportRepository.getReports(from, now);
        return dtoMapper.map(reports, BatchReportResp.class);
    }

    @Override
    public Collection<BatchReportResp> getReportsInDay() {
        var from = Timestamp.valueOf(LocalDateTime.now().plusDays(-1));
        var now = Timestamp.from(Instant.now());
        var reports = batchReportRepository.getReports(from, now);
        return dtoMapper.map(reports, BatchReportResp.class);
    }
}
