package com.bachlinh.order.web.dto.form.admin.email.template;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@RequiredArgsConstructor
@Getter
@Setter
public class EmailTemplateUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("name")
    private String name;

    @JsonAlias("title")
    private String title;

    @JsonAlias("content")
    private String content;

    @JsonAlias("expiry")
    private int expiryPolicy;

    @JsonAlias("params")
    private String[] params;

    @JsonAlias("folder_id")
    private String folderId;
}
