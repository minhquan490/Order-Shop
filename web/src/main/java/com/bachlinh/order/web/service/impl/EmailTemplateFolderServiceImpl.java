package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderCreateForm;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderDeleteForm;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderListResp;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@ServiceComponent
public class EmailTemplateFolderServiceImpl extends AbstractService implements EmailTemplateFolderService {
    private final EmailTemplateFolderRepository emailTemplateFolderRepository;
    private final DtoMapper dtoMapper;
    private final EntityFactory entityFactory;

    private EmailTemplateFolderServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.emailTemplateFolderRepository = resolveRepository(EmailTemplateFolderRepository.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public EmailTemplateFolderInfoResp createEmailTemplateFolder(EmailTemplateFolderCreateForm form, Customer customer) {
        var emailTemplateFolder = entityFactory.getEntity(EmailTemplateFolder.class);
        emailTemplateFolder.setClearTemplatePolicy(form.cleanPolicy());
        emailTemplateFolder.setName(form.name());
        emailTemplateFolder.setOwner(customer);
        emailTemplateFolder = emailTemplateFolderRepository.saveTemplateFolder(emailTemplateFolder);
        return dtoMapper.map(emailTemplateFolder, EmailTemplateFolderInfoResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public EmailTemplateFolderInfoResp updateEmailTemplateFolder(EmailTemplateFolderUpdateForm form, Customer customer) {
        var templateFolder = emailTemplateFolderRepository.getEmailTemplateFolderHasCustomer(form.id());
        if (templateFolder == null) {
            throw new ResourceNotFoundException(String.format("Email template folder with id [%s] not found", form.id()), "");
        }
        if (!templateFolder.getOwner().getId().equals(customer.getId())) {
            throw new AccessDeniedException(String.format("Can not edit email template folder with id [%s], no permission", form.id()));
        }
        templateFolder.setName(form.name());
        templateFolder.setClearTemplatePolicy(form.cleanPolicy() == null ? -1 : form.cleanPolicy());
        templateFolder = emailTemplateFolderRepository.updateTemplateFolder(templateFolder);
        return dtoMapper.map(templateFolder, EmailTemplateFolderInfoResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteEmailTemplateFolder(EmailTemplateFolderDeleteForm form, Customer customer) {
        var folderTemplate = emailTemplateFolderRepository.getEmailTemplateFolderHasCustomer(form.id());
        if (!folderTemplate.getOwner().getId().equals(customer.getId())) {
            throw new AccessDeniedException(String.format("Can not delete folder template has id [%s], no permission", form.id()));
        }
        emailTemplateFolderRepository.deleteTemplateFolder(folderTemplate);
    }

    @Override
    public EmailTemplateFolderInfoResp getEmailTemplateFolderInfo(String templateFolderId, Customer owner) {
        var folder = emailTemplateFolderRepository.getEmailTemplateFolderById(templateFolderId, owner);
        if (folder == null) {
            throw new ResourceNotFoundException(String.format("Folder with id [%s] not found", templateFolderId), "");
        }
        return dtoMapper.map(folder, EmailTemplateFolderInfoResp.class);
    }

    @Override
    public Collection<EmailTemplateFolderListResp> getEmailTemplateFolders(String customerId) {
        var emailTemplates = emailTemplateFolderRepository.getEmailTemplateFolders(customerId);
        return dtoMapper.map(emailTemplates, EmailTemplateFolderListResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new EmailTemplateFolderServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{EmailTemplateFolderService.class};
    }
}
