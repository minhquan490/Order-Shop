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
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplate_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

@RepositoryComponent
@ActiveReflection
public class EmailTemplateRepositoryImpl extends AbstractRepository<EmailTemplate, String> implements EmailTemplateRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailTemplateRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailTemplate.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate) {
        return save(emailTemplate);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate) {
        return save(emailTemplate);
    }

    @Override
    public boolean isEmailTemplateTitleExisted(String title) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailTemplate_.title), title));
        return findOne(spec).isPresent();
    }

    @Override
    public EmailTemplate getEmailTemplateByName(String templateName, Customer owner) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            if (owner == null) {
                return criteriaBuilder.equal(root.get(EmailTemplate_.name), templateName);
            } else {
                var firstStatement = criteriaBuilder.equal(root.get(EmailTemplate_.name), templateName);
                var secondStatement = criteriaBuilder.isNull(root.get(EmailTemplate_.owner));
                return criteriaBuilder.and(firstStatement, secondStatement);
            }
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public EmailTemplate getEmailTemplateById(String id, Customer owner) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            if (owner == null) {
                return criteriaBuilder.equal(root.get(EmailTemplate_.id), id);
            } else {
                var firstStatement = criteriaBuilder.equal(root.get(EmailTemplate_.id), id);
                var secondStatement = criteriaBuilder.isNull(root.get(EmailTemplate_.owner));
                return criteriaBuilder.and(firstStatement, secondStatement);
            }
        });
        return findOne(spec).orElse(null);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
