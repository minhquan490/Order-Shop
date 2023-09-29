package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.entity.model.BatchReport_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.repository.BatchReportRepository;
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
public class BatchReportRepositoryImpl extends AbstractRepository<Integer, BatchReport> implements BatchReportRepository, RepositoryBase {

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
        deleteAllById(reportIds);
    }

    @Override
    public Collection<BatchReport> getReports(Timestamp from, Timestamp to) {
        SqlBuilder sqlBuilder = getSqlBuilder();
        Where timeReportWhere = Where.builder().attribute(BatchReport_.TIME_REPORT).value(new Object[]{from, to}).operation(Operation.BETWEEN).build();
        SqlSelect sqlSelect = sqlBuilder.from(BatchReport.class);
        SqlWhere sqlWhere = sqlSelect.where(timeReportWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        sqlWhere.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        return this.getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Collection<BatchReport> getAllReport() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        OrderBy timeReportOrderBy = OrderBy.builder().column(BatchReport_.TIME_REPORT).type(OrderBy.Type.ASC).build();
        SqlSelect sqlSelect = sqlBuilder.from(BatchReport.class);
        sqlSelect.orderBy(timeReportOrderBy);
        String query = sqlSelect.getNativeQuery();
        return this.getResultList(query, Collections.emptyMap(), getDomainClass());
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new BatchReportRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{BatchReportRepository.class};
    }
}
