package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.CrawlResult;
import com.bachlinh.order.entity.model.CrawlResult_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CrawlResultRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelection;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.query.WhereOperation;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class CrawlResultRepositoryImpl extends AbstractRepository<CrawlResult, Integer> implements CrawlResultRepository {
    private static final LocalTime MID_NIGHT = LocalTime.of(0, 0);

    @DependenciesInitialize
    @ActiveReflection
    public CrawlResultRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(CrawlResult.class, dependenciesResolver);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void saveCrawlResult(CrawlResult crawlResult) {
        save(crawlResult);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteCrawlResult(CrawlResult crawlResult) {
        delete(crawlResult);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteCrawlResult(int id) {
        findById(id).ifPresentOrElse(this::deleteCrawlResult, () -> {
        });
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteCrawlResults(LocalDateTime localDateTime) {
        Specification<CrawlResult> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(CrawlResult_.TIME_FINISH), localDateTime));
        delete(spec);
    }

    @Override
    public Collection<CrawlResult> getCrawlResultToNow() {
        Timestamp night = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), MID_NIGHT));
        Timestamp now = Timestamp.from(Instant.now());
        Where betweenWhere = Where.builder().attribute(CrawlResult_.TIME_FINISH).operator(Operator.BETWEEN).value(new Object[]{night, now}).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(CrawlResult.class);
        WhereOperation whereOperation = sqlSelection.where(betweenWhere);
        String query = whereOperation.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        whereOperation.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        return executeNativeQuery(query, attributes, CrawlResult.class);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
