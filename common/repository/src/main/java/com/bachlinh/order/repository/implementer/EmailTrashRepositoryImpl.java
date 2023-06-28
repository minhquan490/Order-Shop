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
import com.bachlinh.order.entity.model.AbstractEntity_;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailTrashRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@ActiveReflection
@RepositoryComponent
public class EmailTrashRepositoryImpl extends AbstractRepository<EmailTrash, Integer> implements EmailTrashRepository {

    @ActiveReflection
    @DependenciesInitialize
    public EmailTrashRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(EmailTrash.class, dependenciesResolver);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public EmailTrash saveEmailTrash(EmailTrash emailTrash) {
        return save(emailTrash);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public EmailTrash updateTrash(EmailTrash emailTrash) {
        return save(emailTrash);
    }

    @Override
    public void updateTrashes(Collection<EmailTrash> trashes) {
        saveAll(trashes);
    }

    @Override
    public Collection<EmailTrash> getTrashNeedClearing() {
        Timestamp removeTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(-3));
        Specification<EmailTrash> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(AbstractEntity_.modifiedDate), removeTime));
        return findAll(spec);
    }

    @Override
    @ActiveReflection
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
