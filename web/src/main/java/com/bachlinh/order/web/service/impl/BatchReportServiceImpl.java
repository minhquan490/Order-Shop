package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.web.repository.spi.BatchReportRepository;
import com.bachlinh.order.web.dto.resp.BatchReportResp;
import com.bachlinh.order.web.service.common.BatchReportService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

@ServiceComponent
@ActiveReflection
public class BatchReportServiceImpl extends AbstractService implements BatchReportService {
    private final BatchReportRepository batchReportRepository;
    private final DtoMapper dtoMapper;

    private BatchReportServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.batchReportRepository = resolveRepository(BatchReportRepository.class);
        this.dtoMapper = getResolver().resolveDependencies(DtoMapper.class);
    }

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

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new BatchReportServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{BatchReportService.class};
    }
}
