package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.CrawlResult;
import com.bachlinh.order.entity.model.CrawlResult_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.CrawlResultRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CrawlResultRepositoryImpl extends AbstractRepository<Integer, CrawlResult> implements CrawlResultRepository, RepositoryBase {
    private static final LocalTime MID_NIGHT = LocalTime.of(0, 0);

    @DependenciesInitialize
    @ActiveReflection
    public CrawlResultRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(CrawlResult.class, dependenciesResolver);
    }

    @Override
    public void saveCrawlResult(CrawlResult crawlResult) {
        save(crawlResult);
    }

    @Override
    public void deleteCrawlResults(LocalDateTime localDateTime) {
        Select idSelect = Select.builder().column(CrawlResult_.ID).build();
        Where timeFinishWhere = Where.builder().attribute(CrawlResult_.TIME_FINISH).value(localDateTime).operation(Operation.LE).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(CrawlResult.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(timeFinishWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = getResultList(sql, attributes, getDomainClass());
        deleteAll(results);
    }

    @Override
    public Collection<CrawlResult> getCrawlResultToNow() {
        Timestamp night = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), MID_NIGHT));
        Timestamp now = Timestamp.from(Instant.now());
        Where betweenWhere = Where.builder().attribute(CrawlResult_.TIME_FINISH).operation(Operation.BETWEEN).value(new Object[]{night, now}).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(CrawlResult.class);
        SqlWhere sqlWhere = sqlSelect.where(betweenWhere);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        sqlWhere.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        return this.getResultList(query, attributes, CrawlResult.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CrawlResultRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CrawlResultRepository.class};
    }
}
