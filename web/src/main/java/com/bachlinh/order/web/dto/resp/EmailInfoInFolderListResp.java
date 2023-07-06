package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailInfoInFolderListResp {

    @JsonProperty("folder_id")
    private String folderId;

    @JsonProperty("folder_name")
    private String folderName;

    @JsonProperty("emails")
    private EmailInfo[] emails;

    @NoArgsConstructor
    @Getter
    @Setter
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
    }
}
