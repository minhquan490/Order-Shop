package com.bachlinh.order.web.dto.form.admin.setting;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageSettingUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("value")
    private String value;
}
