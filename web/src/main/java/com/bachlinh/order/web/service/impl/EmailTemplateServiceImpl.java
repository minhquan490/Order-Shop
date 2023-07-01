package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
import com.bachlinh.order.repository.EmailTemplateRepository;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateService;

@ActiveReflection
@ServiceComponent
@RequiredArgsConstructor(onConstructor = @__({@ActiveReflection, @DependenciesInitialize}))
public class EmailTemplateServiceImpl implements EmailTemplateService {
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateFolderRepository emailTemplateFolderRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmailTemplateInfoResp saveEmailTemplate(EmailTemplateCreateForm form, Customer owner) {
        var emailTemplate = entityFactory.getEntity(EmailTemplate.class);
        emailTemplate.setName(form.getName());
        emailTemplate.setTitle(form.getTitle());
        emailTemplate.setContent(form.getContent());
        emailTemplate.setExpiryPolicy(form.getTotalMonthAlive());
        emailTemplate.setTotalArgument(form.getParams().length);
        emailTemplate.setParams(String.join(",", form.getParams()));
        emailTemplate.setOwner(owner);
        EmailTemplateFolder folder;
        if (!StringUtils.hasText(form.getFolderId())) {
            folder = emailTemplateFolderRepository.getEmailTemplateFolderByName("Default", owner);
        } else {
            folder = emailTemplateFolderRepository.getEmailTemplateFolderById(form.getFolderId(), owner);
            if (folder == null) {
                throw new ResourceNotFoundException(String.format("Email template folder has id [%s] not found", form.getFolderId()), "");
            }
        }
        emailTemplate.setFolder(folder);
        emailTemplate = emailTemplateRepository.saveEmailTemplate(emailTemplate);
        return dtoMapper.map(emailTemplate, EmailTemplateInfoResp.class);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmailTemplateInfoResp updateEmailTemplate(EmailTemplateUpdateForm form, Customer owner) {
        var emailTemplate = emailTemplateRepository.getEmailTemplateById(form.getId(), owner);
        emailTemplate.setName(form.getName());
        emailTemplate.setTitle(form.getTitle());
        emailTemplate.setContent(form.getContent());
        emailTemplate.setExpiryPolicy(form.getExpiryPolicy());
        emailTemplate.setTotalArgument(form.getParams().length);
        emailTemplate.setParams(String.join(",", form.getParams()));
        var folder = emailTemplateFolderRepository.getEmailTemplateFolderById(form.getFolderId(), owner);
        emailTemplate.setFolder(folder);
        emailTemplate = emailTemplateRepository.updateEmailTemplate(emailTemplate);
        return dtoMapper.map(emailTemplate, EmailTemplateInfoResp.class);
    }
}
