package com.bachlinh.order.setup.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.setup.Setup;
import com.bachlinh.order.setup.spi.AbstractSetup;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@ActiveReflection
public class MessageSettingRegistration extends AbstractSetup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private MessageSettingRepository messageSettingRepository;
    private EntityFactory entityFactory;

    private MessageSettingRegistration(ContainerWrapper wrapper, String profile) {
        super(wrapper, profile);
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
    protected void doBefore() {
        logger.info("BEGIN: Create setting for message");
    }

    @Override
    protected void doExecute() {
        long numberOfMessage = messageSettingRepository.countMessages();
        if (numberOfMessage == 0) {
            createSetting();
        }
    }

    @Override
    protected void doAfter() {
        logger.info("END: Create setting for message");
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

    @Override
    public Setup newInstance(ContainerWrapper wrapper, String profile) {
        return new MessageSettingRegistration(wrapper, profile);
    }
}
