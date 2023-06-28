package com.bachlinh.order.repository.implementer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.entity.model.BatchReport_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.BatchReportRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.sql.Timestamp;
import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class BatchReportRepositoryImpl extends AbstractRepository<BatchReport, Integer> implements BatchReportRepository {

    @DependenciesInitialize
    @ActiveReflection
    public BatchReportRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(BatchReport.class, dependenciesResolver);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void saveAllReport(Collection<BatchReport> reports) {
        saveAll(reports);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteReport(Collection<Integer> reportIds) {
        Page<BatchReport> reports = unionQueryWithId(reportIds, null, Pageable.unpaged());
        deleteAllInBatch(reports.toList());
    }

    @Override
    public Collection<BatchReport> getReports(Timestamp from, Timestamp to) {
        Specification<BatchReport> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(BatchReport_.timeReport), from, to));
        return findAll(spec);
    }
}
