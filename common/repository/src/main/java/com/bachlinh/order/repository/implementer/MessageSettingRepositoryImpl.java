package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.MessageSetting_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class MessageSettingRepositoryImpl extends AbstractRepository<MessageSetting, String> implements MessageSettingRepository {

    @DependenciesInitialize
    @ActiveReflection
    public MessageSettingRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(MessageSetting.class, dependenciesResolver);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public MessageSetting saveMessage(MessageSetting messageSetting) {
        return save(messageSetting);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public MessageSetting updateMessage(MessageSetting messageSetting) {
        return save(messageSetting);
    }

    @Override
    public MessageSetting getMessageById(String id) {
        Select idSelect = Select.builder().column(MessageSetting_.ID).build();
        Select valueSelect = Select.builder().column(MessageSetting_.VALUE).build();
        Where idWhere = Where.builder().attribute(MessageSetting_.ID).value(id).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(MessageSetting.class);
        sqlSelect.select(idSelect).select(valueSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, MessageSetting.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void saveMessages(Collection<MessageSetting> messageSettings) {
        saveAll(messageSettings);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void deleteMessage(String messageId) {
        deleteById(messageId);
    }

    @Override
    public Collection<MessageSetting> getMessages() {
        return findAll();
    }

    @Override
    public long countMessages() {
        return count();
    }

    @Override
    public boolean messageValueExisted(String messageValue) {
        Select idsSelect = Select.builder().column(MessageSetting_.ID).build();
        Where valueWhere = Where.builder().attribute(MessageSetting_.VALUE).value(messageValue).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(MessageSetting.class);
        sqlSelect.select(idsSelect);
        SqlWhere sqlWhere = sqlSelect.where(valueWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, MessageSetting.class);
        return !results.isEmpty();
    }

    @Override
    public boolean isMessageSettingExisted(String id) {
        return getMessageById(id) != null;
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
