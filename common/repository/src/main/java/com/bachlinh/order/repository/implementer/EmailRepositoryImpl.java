package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RepositoryComponent
@ActiveReflection
public class EmailRepositoryImpl extends AbstractRepository<Email, String> implements EmailRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Email.class, containerResolver.getDependenciesResolver());
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
