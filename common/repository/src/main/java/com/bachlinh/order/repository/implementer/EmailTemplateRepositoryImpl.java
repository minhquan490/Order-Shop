package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplate_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.repository.query.CriteriaPredicateParser;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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
    public EmailTemplate getEmailTemplateByName(String templateName, Customer owner) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            var templateWhere = Where.builder().attribute(EmailTemplate_.NAME).value(templateName).operator(Operator.EQ).build();
            var ownerWhere = Where.builder().attribute(EmailTemplate_.OWNER).value(owner).operator(Operator.EQ).build();
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(templateWhere, ownerWhere);
            return extractor.parse();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public EmailTemplate getEmailTemplateById(String id, Customer owner) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            var idWhere = Where.builder().attribute(EmailTemplate_.ID).value(id).operator(Operator.EQ).build();
            var ownerWhere = Where.builder().attribute(EmailTemplate_.OWNER).value(owner).operator(Operator.EQ).build();
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(idWhere, ownerWhere);
            return extractor.parse();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public EmailTemplate getDefaultEmailTemplate(String name) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            var idWhere = Where.builder().attribute(EmailTemplate_.NAME).value(name).operator(Operator.EQ).build();
            var ownerNull = Where.builder().attribute(EmailTemplate_.OWNER).operator(Operator.NULL).build();
            var folderNull = Where.builder().attribute(EmailTemplate_.FOLDER).operator(Operator.NULL).build();
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(idWhere, ownerNull, folderNull);
            return extractor.parse();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public Collection<EmailTemplate> getEmailTemplates(Customer owner) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            var ownerWhere = Where.builder().attribute(EmailTemplate_.OWNER).value(owner).build();
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(ownerWhere);
            return extractor.parse();
        });
        return findAll(spec);
    }

    @Override
    public Collection<EmailTemplate> getEmailTemplates(Collection<String> ids, Customer owner) {
        var idWhere = Where.builder().attribute(EmailTemplate_.ID).value(ids.toArray()).operator(Operator.IN).build();
        var ownerWhere = Where.builder().attribute(EmailTemplate_.OWNER).value(owner).build();
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(idWhere, ownerWhere);
            return extractor.parse();
        });
        return findAll(spec);
    }

    @Override
    public boolean isEmailTemplateExisted(String id, Customer owner) {
        Specification<EmailTemplate> spec = Specification.where((root, query, criteriaBuilder) -> {
            var ownerWhere = Where.builder().attribute(EmailTemplate_.OWNER).value(owner).operator(Operator.EQ).build();
            var idWhere = Where.builder().attribute(EmailTemplate_.ID).value(id).operator(Operator.EQ).build();
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(ownerWhere, idWhere);
            return extractor.parse();
        });
        return exists(spec);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void deleteEmailTemplate(EmailTemplate emailTemplate) {
        delete(emailTemplate);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
