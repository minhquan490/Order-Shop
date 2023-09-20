package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public EmailTemplateFolderInfoResp() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTimeTemplateCleared() {
        return this.timeTemplateCleared;
    }

    public EmailTemplateResp[] getEmails() {
        return this.emails;
    }

    public String getTotalEmail() {
        return this.totalEmail;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("time_template_removed")
    public void setTimeTemplateCleared(String timeTemplateCleared) {
        this.timeTemplateCleared = timeTemplateCleared;
    }

    @JsonProperty("email_templates")
    public void setEmails(EmailTemplateResp[] emails) {
        this.emails = emails;
    }

    @JsonProperty("total_email")
    public void setTotalEmail(String totalEmail) {
        this.totalEmail = totalEmail;
    }

    public static class EmailTemplateResp {

        @JsonProperty("id")
        private String id;

        @JsonAlias("name")
        private String name;

        @JsonAlias("title")
        private String title;

        public EmailTemplateResp() {
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getTitle() {
            return this.title;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonAlias("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonAlias("title")
        public void setTitle(String title) {
            this.title = title;
        }
    }
}
