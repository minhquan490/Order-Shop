package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.MessageSetting_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class MessageSettingRepositoryImpl extends AbstractRepository<String, MessageSetting> implements MessageSettingRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public MessageSettingRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(MessageSetting.class, dependenciesResolver);
    }

    @Override
    public MessageSetting saveMessage(MessageSetting messageSetting) {
        return save(messageSetting);
    }

    @Override
    public MessageSetting updateMessage(MessageSetting messageSetting) {
        return save(messageSetting);
    }

    @Override
    public MessageSetting getMessageById(String id) {
        Select idSelect = Select.builder().column(MessageSetting_.ID).build();
        Select valueSelect = Select.builder().column(MessageSetting_.VALUE).build();
        Where idWhere = Where.builder().attribute(MessageSetting_.ID).value(id).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(MessageSetting.class);
        sqlSelect.select(idSelect).select(valueSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, MessageSetting.class);
    }

    @Override
    public void saveMessages(Collection<MessageSetting> messageSettings) {
        saveAll(messageSettings);
    }

    @Override
    public void deleteMessage(String messageId) {
        deleteById(messageId);
    }

    @Override
    public Collection<MessageSetting> getMessages() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        return getResultList(sqlSelect.getNativeQuery(), Collections.emptyMap(), getDomainClass());
    }

    @Override
    public long countMessages() {
        return count();
    }

    @Override
    public boolean messageValueExisted(String messageValue) {
        Select idsSelect = Select.builder().column(MessageSetting_.ID).build();
        Where valueWhere = Where.builder().attribute(MessageSetting_.VALUE).value(messageValue).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(MessageSetting.class);
        sqlSelect.select(idsSelect);
        SqlWhere sqlWhere = sqlSelect.where(valueWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = this.getResultList(sql, attributes, MessageSetting.class);
        return !results.isEmpty();
    }

    @Override
    public boolean isMessageSettingExisted(String id) {
        return getMessageById(id) != null;
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new MessageSettingRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{MessageSettingRepository.class};
    }
}
