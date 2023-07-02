package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@NoArgsConstructor
@Getter
@Setter
@Dto(forType = "com.bachlinh.order.entity.model.Email")
public class EmailInfoResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "content", outputJsonField = "content")
    private String content;

    @MappedDtoField(targetField = "receivedTime.toString", outputJsonField = "received_time")
    private String receivedTime;

    @MappedDtoField(targetField = "title", outputJsonField = "title")
    private String title;

    @MappedDtoField(targetField = "read", outputJsonField = "is_read")
    private boolean read;

    @MappedDtoField(targetField = "sent", outputJsonField = "is_sent")
    private boolean sent;

    @MappedDtoField(targetField = "mediaType", outputJsonField = "email_type")
    private String mediaType;

    @MappedDtoField(targetField = "toCustomer.getEmail", outputJsonField = "to")
    private String toCustomerName;

    @MappedDtoField(targetField = "folder.getName", outputJsonField = "folder_name")
    private String folderName;
}
