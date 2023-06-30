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
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.entity.model.EmailTemplateFolder_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class EmailTemplateFolderRepositoryImpl extends AbstractRepository<EmailTemplateFolder, String> implements EmailTemplateFolderRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailTemplateFolderRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailTemplateFolder.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public EmailTemplateFolder saveTemplateFolder(EmailTemplateFolder folder) {
        return save(folder);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public EmailTemplateFolder updateTemplateFolder(EmailTemplateFolder folder) {
        return save(folder);
    }

    @Override
    public void deleteTemplateFolder(EmailTemplateFolder folder) {
        delete(folder);
    }

    @Override
    public boolean isEmailTemplateFolderNameExisted(String emailTemplateFolderName) {
        Specification<EmailTemplateFolder> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailTemplateFolder_.name), emailTemplateFolderName));
        return findOne(spec).isPresent();
    }

    @Override
    public boolean isEmailTemplateFolderIdExisted(String id) {
        return existsById(id);
    }

    @Override
    public EmailTemplateFolder getEmailTemplateFolder(String id) {
        Specification<EmailTemplateFolder> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(EmailTemplateFolder_.emailTemplates);
            return criteriaBuilder.equal(root.get(EmailTemplateFolder_.id), id);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public EmailTemplateFolder getEmailTemplateFolderHasCustomer(String id) {
        Specification<EmailTemplateFolder> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(EmailTemplateFolder_.owner);
            return criteriaBuilder.equal(root.get(EmailTemplateFolder_.id), id);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public Collection<EmailTemplateFolder> getEmailTemplateFolders(String customerId) {
        Specification<EmailTemplateFolder> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(EmailTemplateFolder_.emailTemplates);
            root.join(EmailTemplateFolder_.owner);
            return criteriaBuilder.equal(root.get(Customer_.ID), customerId);
        });
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
