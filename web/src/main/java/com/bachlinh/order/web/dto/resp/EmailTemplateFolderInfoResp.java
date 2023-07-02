package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailTemplateFolderInfoResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("time_template_removed")
    private String timeTemplateCleared;

    @JsonProperty("email_templates")
    private EmailTemplateResp[] emails;

    @JsonProperty("total_email")
    private String totalEmail;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class EmailTemplateResp {

        @JsonProperty("id")
        private String id;

        @JsonAlias("name")
        private String name;

        @JsonAlias("title")
        private String title;
    }
}
