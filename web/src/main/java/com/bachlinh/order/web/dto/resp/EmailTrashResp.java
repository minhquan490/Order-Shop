package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

public class EmailTrashResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("emails")
    private Email[] emails;

    public EmailTrashResp() {
    }

    public String getId() {
        return this.id;
    }

    public Email[] getEmails() {
        return this.emails;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("emails")
    public void setEmails(Email[] emails) {
        this.emails = emails;
    }

    @JsonRootName("email")
    public static class Email {

        @JsonProperty("id")
        private String id;

        @JsonProperty("content")
        private String content;

        @JsonProperty("title")
        private String title;

        public String getId() {
            return this.id;
        }

        public String getContent() {
            return this.content;
        }

        public String getTitle() {
            return this.title;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("content")
        public void setContent(String content) {
            this.content = content;
        }

        @JsonProperty("title")
        public void setTitle(String title) {
            this.title = title;
        }
    }
}
