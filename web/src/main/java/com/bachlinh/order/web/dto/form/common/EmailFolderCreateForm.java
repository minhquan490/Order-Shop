package com.bachlinh.order.web.dto.form.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.validate.base.ValidatedDto;

@NoArgsConstructor
@Getter
@Setter
public class EmailFolderCreateForm implements ValidatedDto {

    @JsonAlias("name")
    private String name;
}
