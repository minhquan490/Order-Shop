package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.entity.model.BatchReport_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.BatchReportRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelection;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.query.WhereOperation;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

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
        SqlBuilder sqlBuilder = getSqlBuilder();
        Where timeReportWhere = Where.builder().attribute(BatchReport_.TIME_REPORT).value(new Object[]{from, to}).operator(Operator.BETWEEN).build();
        SqlSelection sqlSelection = sqlBuilder.from(BatchReport.class);
        WhereOperation whereOperation = sqlSelection.where(timeReportWhere);
        String query = whereOperation.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        whereOperation.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        return executeNativeQuery(query, attributes, BatchReport.class);
    }

    @Override
    public Collection<BatchReport> getAllReport() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        OrderBy timeReportOrderBy = OrderBy.builder().column(BatchReport_.TIME_REPORT).type(OrderBy.Type.ASC).build();
        SqlSelection sqlSelection = sqlBuilder.from(BatchReport.class);
        sqlSelection.orderBy(timeReportOrderBy);
        String query = sqlSelection.getNativeQuery();
        return executeNativeQuery(query, Collections.emptyMap(), BatchReport.class);
    }
}
