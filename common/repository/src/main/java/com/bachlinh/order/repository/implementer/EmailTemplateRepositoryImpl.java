package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
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
    public boolean isEmailTemplateTitleExisted(String title) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailTemplate_.title), title));
        return findOne(spec).isPresent();
    }

    @Override
    public EmailTemplate getEmailTemplate(String templateName) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailTemplate_.name), templateName));
        return findOne(spec).orElse(null);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
