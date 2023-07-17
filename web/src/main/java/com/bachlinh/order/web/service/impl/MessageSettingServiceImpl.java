package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingCreateForm;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingDeleteForm;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingUpdateForm;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;
import com.bachlinh.order.web.service.common.MessageSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor
public class MessageSettingServiceImpl implements MessageSettingService {
    private final MessageSettingRepository messageSettingRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public MessageSettingResp saveMessageSetting(MessageSettingCreateForm form) {
        var entity = entityFactory.getEntity(MessageSetting.class);
        entity.setValue(form.getValue());
        entity = messageSettingRepository.saveMessage(entity);
        return dtoMapper.map(entity, MessageSettingResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public MessageSettingResp updateMessageSetting(MessageSettingUpdateForm form) {
        var entity = messageSettingRepository.getMessageById(form.getId());
        entity.setValue(form.getValue());
        entity = messageSettingRepository.updateMessage(entity);
        return dtoMapper.map(entity, MessageSettingResp.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void deleteMessageSetting(MessageSettingDeleteForm form) {
        messageSettingRepository.deleteMessage(form.getId());
    }

    @Override
    public Collection<MessageSettingResp> getAllMessage() {
        return dtoMapper.map(messageSettingRepository.getMessages(), MessageSettingResp.class);
    }
}
