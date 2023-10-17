package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.entity.model.DirectMessage_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.DirectMessageRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class DirectMessageRepositoryImpl extends AbstractRepository<Integer, DirectMessage> implements DirectMessageRepository, RepositoryBase {
    private static final Integer REMOVAL_POLICY = -3;

    @ActiveReflection
    @DependenciesInitialize
    public DirectMessageRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(DirectMessage.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public void saveMessage(DirectMessage message) {
        save(message);
    }

    @Override
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
