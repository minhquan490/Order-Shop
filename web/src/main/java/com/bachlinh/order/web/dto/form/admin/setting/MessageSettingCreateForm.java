package com.bachlinh.order.web.dto.form.admin.setting;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageSettingCreateForm implements ValidatedDto {

    @JsonAlias("value")
    private String value;
}
