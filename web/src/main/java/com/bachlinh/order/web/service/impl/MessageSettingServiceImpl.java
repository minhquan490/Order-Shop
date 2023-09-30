package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingCreateForm;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingDeleteForm;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingUpdateForm;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.MessageSettingService;

import java.util.Collection;

@ServiceComponent
@ActiveReflection
public class MessageSettingServiceImpl extends AbstractService implements MessageSettingService {
    private final MessageSettingRepository messageSettingRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    private MessageSettingServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.messageSettingRepository = resolveRepository(MessageSettingRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
    }

    @Override
    public MessageSettingResp saveMessageSetting(MessageSettingCreateForm form) {
        var entity = entityFactory.getEntity(MessageSetting.class);
        entity.setValue(form.getValue());
        entity = messageSettingRepository.saveMessage(entity);
        return dtoMapper.map(entity, MessageSettingResp.class);
    }

    @Override
    public MessageSettingResp updateMessageSetting(MessageSettingUpdateForm form) {
        var entity = messageSettingRepository.getMessageById(form.getId());
        entity.setValue(form.getValue());
        entity = messageSettingRepository.updateMessage(entity);
        return dtoMapper.map(entity, MessageSettingResp.class);
    }

    @Override
    public void deleteMessageSetting(MessageSettingDeleteForm form) {
        messageSettingRepository.deleteMessage(form.getId());
    }

    @Override
    public MessageSettingResp getMessage(String messageId) {
        var entity = messageSettingRepository.getMessageById(messageId);
        return dtoMapper.map(entity, MessageSettingResp.class);
    }

    @Override
    public Collection<MessageSettingResp> getAllMessage() {
        return dtoMapper.map(messageSettingRepository.getMessages(), MessageSettingResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new MessageSettingServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{MessageSettingService.class};
    }
}
