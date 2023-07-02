package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
public class AddEmailToTrashForm implements ValidatedDto {

    @JsonAlias("email_ids")
    private String[] emailIds;
}
