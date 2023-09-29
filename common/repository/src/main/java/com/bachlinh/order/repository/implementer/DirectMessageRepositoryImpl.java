package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.entity.model.DirectMessage_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.DirectMessageRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class DirectMessageRepositoryImpl extends AbstractRepository<Integer, DirectMessage> implements DirectMessageRepository, RepositoryBase {
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
        deleteAll(directMessages);
    }

    @Override
    public Collection<DirectMessage> getDirectForRemove() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime finalLocalDateTime = localDateTime.plusYears(REMOVAL_POLICY);
        Select directMessageIdSelect = Select.builder().column(DirectMessage_.ID).build();
        Where removeTimeWhere = Where.builder().attribute(DirectMessage_.TIME_SENT).value(finalLocalDateTime).operation(Operation.LE).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(DirectMessage.class);
        sqlSelect.select(directMessageIdSelect);
        SqlWhere sqlWhere = sqlSelect.where(removeTimeWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, DirectMessage.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new DirectMessageRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{DirectMessageRepository.class};
    }
}
