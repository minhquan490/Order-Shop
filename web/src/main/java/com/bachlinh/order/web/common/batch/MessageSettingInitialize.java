package com.bachlinh.order.web.common.batch;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.web.repository.spi.MessageSettingRepository;

@BatchJob(name = "messageSettingInitialize")
public class MessageSettingInitialize extends AbstractJob {

    private final LocalDateTime now = LocalDateTime.now();

    private MessageSettingRepository messageSettingRepository;
    private EntityFactory entityFactory;

    private MessageSettingInitialize(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new MessageSettingInitialize(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (messageSettingRepository == null) {
            messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    protected void doExecuteInternal() {
        long numberOfMessage = messageSettingRepository.countMessages();
        if (numberOfMessage == 0) {
            createSetting();
        }
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return doGetPreviousExecutionTime();
    }

    @Override
    protected LocalDateTime doGetPreviousExecutionTime() {
        return now;
    }

    @Override
    public JobType getJobType() {
        return JobType.ONCE;
    }

    private void createSetting() {
        Collection<MessageSetting> messages = createSettingContent().stream()
                .map(s -> {
                    var setting = entityFactory.getEntity(MessageSetting.class);
                    setting.setValue(s);
                    return setting;
                })
                .toList();
        messageSettingRepository.saveMessages(messages);
    }

    private Collection<String> createSettingContent() {
        List<String> messages = new LinkedList<>();
        messages.add("{0}: must not empty");
        messages.add("Length of {0} must not be less than or equals {1} character");
        messages.add("{0} must be not null");
        messages.add("Product: Cart detail must be contain product");
        messages.add("Cart: Cart detail must be in cart");
        messages.add("Phone number: must have 10 number");
        messages.add("{0}: is existed");
        messages.add("{0}: not found");
        messages.add("{0}: is invalid");
        messages.add("{0}: must be in range {1} - {2}");
        messages.add("Can not identity {0} with id [{1}] for {2}");
        messages.add("{0}: must be positive");
        messages.add("{0}: must be negative");
        messages.add("{0}: must be specify");
        messages.add("{0}: must have at least 1 param");
        messages.add("{0}: must be choice");
        messages.add("{0}: is not existed");
        messages.add("{0}: must be a number");
        messages.add("Can not perform searching {0}");
        messages.add("{0} be enable of disable");
        messages.add("Gender: must be a male or female");// 21
        messages.add("Categories: product missing category");
        messages.add("Time start: Must be greater or equals current time");
        messages.add("Time end: Must be greater than time to start");
        messages.add("Can not add unknown email to trash");
        messages.add("Can not perform search with empty query");
        messages.add("Params for template must not be specify");
        messages.add("Product must have category");
        messages.add("Can not store email");
        messages.add("Can not find email template for sending");
        messages.add("Receiver must specify");
        messages.add("{0} associate with {1} must not be null");
        messages.add("Can not access url {0}, no permission");
        messages.add("File size limit exceeded");
        messages.add("{0}: Not allow for update");
        return messages;
    }
}
