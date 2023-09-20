package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

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

    @MappedDtoField(targetField = "mediaType", outputJsonField = "email_type")
    private String mediaType;

    @MappedDtoField(targetField = "toCustomer.getEmail", outputJsonField = "to")
    private String toCustomerName;

    @MappedDtoField(targetField = "folder.getName", outputJsonField = "folder_name")
    private String folderName;

    public EmailInfoResp() {
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

    public boolean isRead() {
        return this.read;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public String getToCustomerName() {
        return this.toCustomerName;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setToCustomerName(String toCustomerName) {
        this.toCustomerName = toCustomerName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
