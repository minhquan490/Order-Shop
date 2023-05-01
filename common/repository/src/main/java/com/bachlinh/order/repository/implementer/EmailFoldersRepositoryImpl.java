package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailFolders_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
class EmailFoldersRepositoryImpl extends AbstractRepository<EmailFolders, String> implements EmailFoldersRepository {

    @Autowired
    EmailFoldersRepositoryImpl(Class<EmailFolders> domainClass, DependenciesContainerResolver containerResolver) {
        super(domainClass, containerResolver.getDependenciesResolver());
    }

    @Override
    public boolean isFolderExisted(String folderName) {
        Specification<EmailFolders> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailFolders_.name), folderName));
        return findOne(spec).isPresent();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public EmailFolders saveEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public EmailFolders updateEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    public void delete(String id) {
        deleteById(id);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
