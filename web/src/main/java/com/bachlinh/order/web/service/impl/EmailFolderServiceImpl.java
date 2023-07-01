package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.web.service.common.EmailFolderService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__({@ActiveReflection, @DependenciesInitialize}))
public class EmailFolderServiceImpl implements EmailFolderService {
    private static final List<String> DEFAULT_FOLDER = List.of("Default", "Draft", "Sent", "Importance");

    private final EmailFoldersRepository emailFoldersRepository;
    private final EntityFactory entityFactory;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void createDefaultFolders(Customer owner) {
        Collection<EmailFolders> emailFolders = new HashSet<>(DEFAULT_FOLDER.size());
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
}
