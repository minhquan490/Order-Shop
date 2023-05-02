package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.entity.model.EmailTemplateFolder_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;

@RepositoryComponent
@ActiveReflection
public class EmailTemplateFolderRepositoryImpl extends AbstractRepository<EmailTemplateFolder, String> implements EmailTemplateFolderRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailTemplateFolderRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailTemplateFolder.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public boolean isEmailTemplateFolderExisted(String emailTemplateFolderName) {
        Specification<EmailTemplateFolder> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmailTemplateFolder_.name), emailTemplateFolderName));
        return findOne(spec).isPresent();
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
