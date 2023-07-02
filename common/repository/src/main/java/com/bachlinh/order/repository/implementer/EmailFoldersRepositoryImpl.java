package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailFolders_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class EmailFoldersRepositoryImpl extends AbstractRepository<EmailFolders, String> implements EmailFoldersRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailFoldersRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailFolders.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public boolean isFolderExisted(String folderName, Customer owner) {
        Specification<EmailFolders> spec = Specification.where((root, query, criteriaBuilder) -> {
            var firstStatement = criteriaBuilder.equal(root.get(EmailFolders_.name), folderName);
            var secondStatement = criteriaBuilder.equal(root.get(EmailFolders_.owner), owner);
            return criteriaBuilder.and(firstStatement, secondStatement);
        });
        return exists(spec);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public EmailFolders saveEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    public EmailFolders getEmailFolderByName(String name, Customer owner) {
        Specification<EmailFolders> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(EmailFolders_.owner);
            var firstStatement = criteriaBuilder.equal(root.get(EmailFolders_.name), name);
            var secondStatement = criteriaBuilder.equal(root.get(Customer_.ID), owner.getId());
            return criteriaBuilder.and(firstStatement, secondStatement);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public EmailFolders getEmailFolderById(String id, Customer owner) {
        Specification<EmailFolders> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(EmailFolders_.owner);
            var firstStatement = criteriaBuilder.equal(root.get(EmailFolders_.id), id);
            var secondStatement = criteriaBuilder.equal(root.get(Customer_.ID), owner.getId());
            return criteriaBuilder.and(firstStatement, secondStatement);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public EmailFolders updateEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void bulkSave(Collection<EmailFolders> folders) {
        saveAll(folders);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void delete(String id) {
        deleteById(id);
    }

    @Override
    public Collection<EmailFolders> getEmailFoldersOfCustomer(Customer owner) {
        Specification<EmailFolders> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailFolders_.owner), owner));
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
