package com.bachlinh.order.web.dto.form.admin.email.sending;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class NormalEmailSendingForm implements ValidatedDto {

    @JsonAlias("title")
    private String title;

    @JsonAlias("content")
    private String content;

    @JsonAlias("content_type")
    private String contentType;

    @JsonAlias("to")
    private String toCustomer;
}
