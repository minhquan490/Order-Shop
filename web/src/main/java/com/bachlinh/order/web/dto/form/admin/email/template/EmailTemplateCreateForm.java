package com.bachlinh.order.web.dto.form.admin.email.template;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@Getter
@Setter
@NoArgsConstructor
public class EmailTemplateCreateForm implements ValidatedDto {

    @JsonAlias("name")
    private String name;

    @JsonAlias("title")
    private String title;

    @JsonAlias("content")
    private String content;

    @JsonAlias("expire")
    private int totalMonthAlive;

    @JsonAlias("folder")
    private String folderId;

    @JsonAlias("params")
    private String[] params;
}
