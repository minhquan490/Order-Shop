package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.MessageSetting_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.QueryExtractor;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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
        return findById(id).orElse(null);
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
        Where valueWhere = Where.builder().attribute(MessageSetting_.VALUE).value(messageValue).operator(Operator.EQ).build();
        Specification<MessageSetting> spec = Specification.where((root, query, criteriaBuilder) -> {
            var queryExtractor = new QueryExtractor(criteriaBuilder, query, root);
            queryExtractor.where(valueWhere);
            return queryExtractor.extract();
        });
        return exists(spec);
    }

    @Override
    public boolean isMessageSettingExisted(String id) {
        return existsById(id);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
