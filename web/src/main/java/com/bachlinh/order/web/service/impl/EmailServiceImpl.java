package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.EmailRepository;
import com.bachlinh.order.repository.EmailTrashRepository;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;
import com.bachlinh.order.web.dto.resp.EmailInfoInFolderListResp;
import com.bachlinh.order.web.dto.resp.EmailInfoResp;
import com.bachlinh.order.web.dto.resp.EmailSendingResp;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import com.bachlinh.order.web.service.business.EmailSearchingService;
import com.bachlinh.order.web.service.business.EmailSendingService;
import com.bachlinh.order.web.service.common.EmailService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__({@ActiveReflection, @DependenciesInitialize}))
public class EmailServiceImpl implements EmailSendingService, EmailService, EmailInTrashService, EmailSearchingService {
    private final EmailRepository emailRepository;
    private final CustomerRepository customerRepository;
    private final EmailFoldersRepository emailFoldersRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;
    private final EmailTrashRepository emailTrashRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public EmailSendingResp sendNormalEmail(NormalEmailSendingForm form, Customer sender) {
        var email = entityFactory.getEntity(Email.class);
        email.setContent(form.getContent());
        email.setReceivedTime(Timestamp.from(Instant.now()));
        email.setTitle(form.getTitle());
        email.setRead(false);
        email.setSent(true);
        email.setMediaType(form.getContentType());
        var toCustomer = customerRepository.getCustomerById(form.getToCustomer(), false);
        email.setToCustomer(toCustomer);
        email.setFromCustomer(sender);
        var folder = emailFoldersRepository.getEmailFolderByName("Default", toCustomer);
        email.setFolder(folder);
        email.setTimeSent(Timestamp.from(Instant.now()));
        email = emailRepository.saveEmail(email);
        return dtoMapper.map(email, EmailSendingResp.class);
    }

    @Override
    public EmailInfoResp getEmailOfCustomer(String id, Customer owner) {
        var email = emailRepository.getEmailById(id, owner);
        return dtoMapper.map(email, EmailInfoResp.class);
    }

    @Override
    public EmailInfoInFolderListResp getEmailsOfCustomer(String folderId, Customer owner) {
        var emails = emailRepository.getEmailsByFolderId(folderId, owner);
        var emailFolder = entityFactory.getEntity(EmailFolders.class);
        emailFolder.setId(emails.get(0).getFolder().getId());
        emailFolder.setName(emails.get(0).getFolder().getName());
        emailFolder.setEmails(new HashSet<>(emails));
        return dtoMapper.map(emailFolder, EmailInfoInFolderListResp.class);
    }

    @Override
    public EmailTrashResp addEmailToTrash(String emailId, Customer owner) {
        var email = emailRepository.getEmailById(emailId, owner);
        if (email == null) {
            throw new ResourceNotFoundException("Email not found", "");
        }
        var trash = emailTrashRepository.getTrashOfCustomer(owner);
        trash.getEmails().add(email);
        trash = emailTrashRepository.updateTrash(trash);
        return dtoMapper.map(trash, EmailTrashResp.class);
    }

    @Override
    public EmailTrashResp addEmailsToTrash(Collection<String> emailIds, Customer owner) {
        var emails = emailRepository.getAllEmailByIds(emailIds);
        var trash = emailTrashRepository.getTrashOfCustomer(owner);
        trash.getEmails().addAll(emails);
        emailTrashRepository.updateTrash(trash);
        return dtoMapper.map(trash, EmailTrashResp.class);
    }

    @Override
    public EmailTrashResp getEmailsInTrash(Customer owner) {
        var trash = emailTrashRepository.getTrashOfCustomer(owner);
        return dtoMapper.map(trash, EmailTrashResp.class);
    }

    @Override
    public EmailTrashResp restoreEmailFromTrash(Collection<String> emailIds, Customer owner) {
        var trash = emailTrashRepository.getTrashOfCustomer(owner);
        var emails = emailRepository.getAllEmailByIds(emailIds);
        trash.getEmails().removeAll(emails);
        trash = emailTrashRepository.updateTrash(trash);
        return dtoMapper.map(trash, EmailTrashResp.class);
    }

    @Override
    public void removeEmailsFromTrash(Collection<String> emailIds, Customer owner) {
        var emails = emailRepository.getAllEmailByIds(emailIds);
        var trash = emailTrashRepository.getTrashOfCustomer(owner);
        emails.forEach(email -> trash.getEmails().remove(email));
        emailTrashRepository.updateTrash(trash);
        emailRepository.deleteEmails(emailIds);
    }

    @Override
    public Collection<EmailInfoResp> searchEmail(String query) {
        var context = entityFactory.getEntityContext(Email.class);
        var ids = context.search(Email.class, query);
        var result = emailRepository.getAllEmailByIds(ids);
        return dtoMapper.map(result, EmailInfoResp.class);
    }
}
