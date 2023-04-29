package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.DirectMessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
class DirectMessageRepositoryImpl extends AbstractRepository<DirectMessage, Integer> implements DirectMessageRepository {
    DirectMessageRepositoryImpl(ApplicationContext applicationContext) {
        super(DirectMessage.class, applicationContext);
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
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
