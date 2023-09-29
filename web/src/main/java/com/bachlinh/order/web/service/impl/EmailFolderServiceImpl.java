package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.web.dto.form.common.EmailFolderCreateForm;
import com.bachlinh.order.web.dto.form.common.EmailFolderUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailFolderService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@ServiceComponent
public class EmailFolderServiceImpl extends AbstractService implements EmailFolderService {
    private static final List<String> DEFAULT_FOLDER = List.of("Default", "Draft", "Sent", "Importance");

    private final EmailFoldersRepository emailFoldersRepository;
    private final EntityFactory entityFactory;

    private EmailFolderServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.emailFoldersRepository = resolveRepository(EmailFoldersRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void createDefaultFolders(Customer owner) {
        Collection<EmailFolders> emailFolders = HashSet.newHashSet(DEFAULT_FOLDER.size());
        DEFAULT_FOLDER.forEach(name -> {
            var folder = entityFactory.getEntity(EmailFolders.class);
            folder.setTimeCreated(Timestamp.from(Instant.now()));
            folder.setOwner(owner);
            folder.setName(name);
            folder.setEmailClearPolicy(-1);
            emailFolders.add(folder);
        });
        emailFoldersRepository.bulkSave(emailFolders);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void deleteEmailFolder(String folderId) {
        emailFoldersRepository.delete(folderId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmailFolderInfoResp createEmailFolder(EmailFolderCreateForm form, Customer owner) {
        var folder = entityFactory.getEntity(EmailFolders.class);
        folder.setName(folder.getName());
        folder.setTimeCreated(Timestamp.from(Instant.now()));
        folder = emailFoldersRepository.saveEmailFolder(folder);
        return new EmailFolderInfoResp(folder.getId(), folder.getName(), folder.getEmailClearPolicy());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public EmailFolderInfoResp updateEmailFolder(EmailFolderUpdateForm form, Customer owner) {
        var folder = emailFoldersRepository.getEmailFolderById(form.getId(), owner);
        folder.setName(form.getName());
        folder.setEmailClearPolicy(form.getCleanPolicy());
        folder = emailFoldersRepository.updateEmailFolder(folder);
        return new EmailFolderInfoResp(folder.getId(), folder.getName(), folder.getEmailClearPolicy());
    }

    @Override
    public Collection<EmailFolderInfoResp> getEmailFoldersOfCustomer(Customer owner) {
        var folders = emailFoldersRepository.getEmailFoldersOfCustomer(owner);
        return folders.stream()
                .map(emailFolders -> new EmailFolderInfoResp(emailFolders.getId(), emailFolders.getName(), emailFolders.getEmailClearPolicy()))
                .toList();
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new EmailFolderServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{EmailFolderService.class};
    }
}
