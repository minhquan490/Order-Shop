package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.entity.model.EmailTemplateFolder_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
class EmailTemplateFolderRepositoryImpl extends AbstractRepository<EmailTemplateFolder, String> implements EmailTemplateFolderRepository {

    @Autowired
    EmailTemplateFolderRepositoryImpl(DependenciesContainerResolver containerResolver) {
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
