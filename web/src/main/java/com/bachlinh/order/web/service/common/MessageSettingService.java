package com.bachlinh.order.web.service.common;

import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingCreateForm;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingDeleteForm;
import com.bachlinh.order.web.dto.form.admin.setting.MessageSettingUpdateForm;
import com.bachlinh.order.web.dto.resp.MessageSettingResp;

import java.util.Collection;

public interface MessageSettingService {
    MessageSettingResp saveMessageSetting(MessageSettingCreateForm form);

    MessageSettingResp updateMessageSetting(MessageSettingUpdateForm form);

    void deleteMessageSetting(MessageSettingDeleteForm form);

    Collection<MessageSettingResp> getAllMessage();
}
