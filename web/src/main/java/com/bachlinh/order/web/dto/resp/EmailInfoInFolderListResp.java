package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailInfoInFolderListResp {

    @JsonProperty("folder_id")
    private String folderId;

    @JsonProperty("folder_name")
    private String folderName;

    @JsonProperty("emails")
    private EmailInfo[] emails;

    public EmailInfoInFolderListResp() {
    }

    public String getFolderId() {
        return this.folderId;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public EmailInfo[] getEmails() {
        return this.emails;
    }

    @JsonProperty("folder_id")
    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    @JsonProperty("folder_name")
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    @JsonProperty("emails")
    public void setEmails(EmailInfo[] emails) {
        this.emails = emails;
    }

    public static class EmailInfo {

        @JsonProperty("id")
        private String id;

        @JsonProperty("content")
        private String content;

        @JsonProperty("received_time")
        private String receivedTime;

        @JsonProperty("title")
        private String title;

        @JsonProperty("sender_name")
        private String senderName;

        @JsonProperty("sender_email")
        private String senderEmail;

        @JsonProperty("media_type")
        private String mediaType;

        public EmailInfo() {
        }

        public String getId() {
            return this.id;
        }

        public String getContent() {
            return this.content;
        }

        public String getReceivedTime() {
            return this.receivedTime;
        }

        public String getTitle() {
            return this.title;
        }

        public String getSenderName() {
            return this.senderName;
        }

        public String getSenderEmail() {
            return this.senderEmail;
        }

        public String getMediaType() {
            return this.mediaType;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("content")
        public void setContent(String content) {
            this.content = content;
        }

        @JsonProperty("received_time")
        public void setReceivedTime(String receivedTime) {
            this.receivedTime = receivedTime;
        }

        @JsonProperty("title")
        public void setTitle(String title) {
            this.title = title;
        }

        @JsonProperty("sender_name")
        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        @JsonProperty("sender_email")
        public void setSenderEmail(String senderEmail) {
            this.senderEmail = senderEmail;
        }

        @JsonProperty("media_type")
        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }
    }
}
