package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.Email")
public class EmailSendingResp {

    @MappedDtoField(targetField = "title", outputJsonField = "title")
    private String title;

    @MappedDtoField(targetField = "sent", outputJsonField = "is_sent")
    private boolean sent;

    @MappedDtoField(targetField = "timeSent.toString", outputJsonField = "time_sent")
    private String timeSent;

    public EmailSendingResp() {
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isSent() {
        return this.sent;
    }

    public String getTimeSent() {
        return this.timeSent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
