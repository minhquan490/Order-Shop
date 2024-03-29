package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.BadVariableException;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.mail.template.BindingModel;
import com.bachlinh.order.mail.template.EmailTemplateProcessor;
import com.bachlinh.order.web.repository.spi.CustomerRepository;
import com.bachlinh.order.web.repository.spi.EmailFoldersRepository;
import com.bachlinh.order.web.repository.spi.EmailRepository;
import com.bachlinh.order.web.repository.spi.EmailTemplateFolderRepository;
import com.bachlinh.order.web.repository.spi.EmailTemplateRepository;
import com.bachlinh.order.web.dto.form.admin.email.sending.TemplateMailSendingForm;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateDeleteForm;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateSearchForm;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.business.EmailTemplateSearchService;
import com.bachlinh.order.web.service.business.EmailTemplateSendingService;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@ActiveReflection
@ServiceComponent
public class EmailTemplateServiceImpl extends AbstractService implements EmailTemplateService, EmailTemplateSendingService, EmailTemplateSearchService {
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateFolderRepository emailTemplateFolderRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;
    private final EmailTemplateProcessor emailTemplateProcessor;
    private final CustomerRepository customerRepository;
    private final EmailFoldersRepository emailFoldersRepository;
    private final EmailRepository emailRepository;

    private EmailTemplateServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.emailTemplateRepository = resolveRepository(EmailTemplateRepository.class);
        this.emailTemplateFolderRepository = resolveRepository(EmailTemplateFolderRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
        this.emailTemplateProcessor = resolver.resolveDependencies(EmailTemplateProcessor.class);
        this.customerRepository = resolveRepository(CustomerRepository.class);
        this.emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        this.emailRepository = resolveRepository(EmailRepository.class);
    }

    @Override
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

    @Override
    public EmailTemplateInfoResp getEmailTemplateById(String templateId, Customer owner) {
        var template = emailTemplateRepository.getEmailTemplateById(templateId, owner);
        return dtoMapper.map(template, EmailTemplateInfoResp.class);
    }

    @Override
    public Collection<EmailTemplateInfoResp> getEmailTemplates(Customer owner) {
        var emailTemplates = emailTemplateRepository.getEmailTemplates(owner);
        return dtoMapper.map(emailTemplates, EmailTemplateInfoResp.class);
    }

    @Override
    public void deleteEmailTemplate(EmailTemplateDeleteForm form, Customer owner) {
        var template = emailTemplateRepository.getEmailTemplateById(form.id(), owner);
        emailTemplateRepository.deleteEmailTemplate(template);
    }

    @Override
    public void processTemplateAndSend(TemplateMailSendingForm form, Customer templateOwner, Environment environment) {
        var template = emailTemplateRepository.getEmailTemplateById(form.getTemplateId(), templateOwner);
        if (!template.getTotalArgument().equals(form.getParams().length)) {
            throw new BadVariableException("Total param not match with template, can not send email");
        }
        var model = BindingModel.getInstance();
        for (var param : form.getParams()) {
            model.put(param.getName(), param.getValue());
        }
        String processedEmail = emailTemplateProcessor.process(template.getContent(), model);
        var sentEmail = entityFactory.getEntity(Email.class);
        sentEmail.setContent(processedEmail);
        sentEmail.setReceivedTime(Timestamp.from(Instant.now()));
        sentEmail.setTitle(template.getTitle());
        sentEmail.setRead(false);
        sentEmail.setSent(true);
        sentEmail.setMediaType(MediaType.TEXT_HTML_VALUE);
        sentEmail.setFromCustomer(templateOwner);
        var toCustomer = customerRepository.getCustomerForEmailSending(form.getToCustomer());
        sentEmail.setToCustomer(toCustomer);
        var folder = emailFoldersRepository.getEmailFolderByName("Default", toCustomer);
        sentEmail.setFolder(folder);
        sentEmail.setTimeSent(Timestamp.from(Instant.now()));
        emailRepository.saveEmail(sentEmail);
    }

    @Override
    public Collection<EmailTemplateInfoResp> search(EmailTemplateSearchForm form, Customer owner) {
        var context = entityFactory.getEntityContext(EmailTemplate.class);
        var ids = context.search(form.getQuery());
        var result = emailTemplateRepository.getEmailTemplates(ids, owner);
        return dtoMapper.map(result, EmailTemplateInfoResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new EmailTemplateServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{EmailTemplateService.class, EmailTemplateSendingService.class, EmailTemplateSearchService.class};
    }
}
