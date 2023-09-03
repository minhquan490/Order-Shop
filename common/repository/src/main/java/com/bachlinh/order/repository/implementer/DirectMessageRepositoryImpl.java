package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.entity.model.DirectMessage_;
import com.bachlinh.order.repository.DirectMessageRepository;
import com.bachlinh.order.repository.adapter.AbstractRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class DirectMessageRepositoryImpl extends AbstractRepository<DirectMessage, Integer> implements DirectMessageRepository {
    private static final Integer REMOVAL_POLICY = -3;

    @ActiveReflection
    @DependenciesInitialize
    public DirectMessageRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(DirectMessage.class, containerResolver.getDependenciesResolver());
    }

    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    @Override
    public void saveMessage(DirectMessage message) {
        save(message);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteMessage(Collection<DirectMessage> directMessages) {
        getEntityManager().flush();
        deleteAllByIdInBatch(directMessages.stream().map(DirectMessage::getId).toList());
    }

    @Override
    public Collection<DirectMessage> getDirectForRemove() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime finalLocalDateTime = localDateTime.plusYears(REMOVAL_POLICY);
        Select directMessageIdSelect = Select.builder().column(DirectMessage_.ID).build();
        Where removeTimeWhere = Where.builder().attribute(DirectMessage_.TIME_SENT).value(finalLocalDateTime).operator(Operator.LE).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(DirectMessage.class);
        sqlSelect.select(directMessageIdSelect);
        SqlWhere sqlWhere = sqlSelect.where(removeTimeWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return executeNativeQuery(sql, attributes, DirectMessage.class);
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
