package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders_;
import com.bachlinh.order.entity.model.Email_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.Collection;
import java.util.List;

@RepositoryComponent
@ActiveReflection
public class EmailRepositoryImpl extends AbstractRepository<Email, String> implements EmailRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Email.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Email saveEmail(Email email) {
        return save(email);
    }

    @Override
    public Email getEmailById(String id, Customer owner) {
        Specification<Email> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(Email_.toCustomer);
            root.join(Email_.folder);
            var firstStatement = criteriaBuilder.equal(root.get(Email_.id), id);
            var secondStatement = criteriaBuilder.equal(root.get(Email_.fromCustomer), owner);
            var thirdStatement = criteriaBuilder.isNull(root.get(Email_.emailTrash));
            return criteriaBuilder.and(firstStatement, secondStatement, thirdStatement);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public Collection<Email> getAllEmailByIds(Iterable<String> ids) {
        return findAllById(ids);
    }

    @Override
    public List<Email> getEmailsByFolderId(String folderId, Customer owner) {
        Specification<Email> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(Email_.folder);
            root.join(Email_.fromCustomer);
            var firstStatement = criteriaBuilder.equal(root.get(EmailFolders_.ID), folderId);
            var secondStatement = criteriaBuilder.equal(root.get(Email_.toCustomer), owner);
            return criteriaBuilder.and(firstStatement, secondStatement);
        });
        return findAll(spec, Sort.by(Email_.RECEIVED_TIME).descending());
    }

    @Override
    public void deleteEmails(Collection<String> ids) {
        deleteAllById(ids);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
