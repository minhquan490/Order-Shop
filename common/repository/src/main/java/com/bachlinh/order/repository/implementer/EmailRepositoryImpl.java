package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Primary
class EmailRepositoryImpl extends AbstractRepository<Email, String> implements EmailRepository {

    @Autowired
    EmailRepositoryImpl(Class<Email> domainClass, ApplicationContext applicationContext) {
        super(domainClass, applicationContext);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Email saveEmail(Email email) {
        return save(email);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
