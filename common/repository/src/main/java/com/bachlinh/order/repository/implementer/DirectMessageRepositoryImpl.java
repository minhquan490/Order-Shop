package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.entity.model.DirectMessage_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.DirectMessageRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class DirectMessageRepositoryImpl extends AbstractRepository<DirectMessage, Integer> implements DirectMessageRepository {
    private static final Integer REMOVAL_POLICY = -3;

    @ActiveReflection
    @DependenciesInitialize
    public DirectMessageRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(DirectMessage.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public DirectMessage updateMessage(DirectMessage message) {
        return save(message);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public DirectMessage saveMessage(DirectMessage message) {
        return save(message);
    }

    @Override
    public void deleteMessage(int id) {
        deleteById(id);
    }

    @Override
    public void deleteMessage(Collection<DirectMessage> directMessages) {
        getEntityManager().flush();
        deleteAllByIdInBatch(directMessages.stream().map(DirectMessage::getId).toList());
    }

    @Override
    public Collection<DirectMessage> getDirectForRemove() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime finalLocalDateTime = localDateTime.plusYears(REMOVAL_POLICY);
        Specification<DirectMessage> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(DirectMessage_.timeSent), Timestamp.valueOf(finalLocalDateTime)));
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
