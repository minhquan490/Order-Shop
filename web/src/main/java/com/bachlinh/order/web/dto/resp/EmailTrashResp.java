package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EmailTrashResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("emails")
    private Email[] emails;

    @JsonRootName("email")
    @Getter
    @Setter
    public static class Email {

        @JsonProperty("id")
        private String id;

        @JsonProperty("content")
        private String content;

        @JsonProperty("title")
        private String title;
    }
}
