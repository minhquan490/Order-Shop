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
public class EmailSendingResp {

    @MappedDtoField(targetField = "title", outputJsonField = "title")
    private String title;

    @MappedDtoField(targetField = "sent", outputJsonField = "is_sent")
    private boolean sent;

    @MappedDtoField(targetField = "timeSent.toString", outputJsonField = "time_sent")
    private String timeSent;
}
