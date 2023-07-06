package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Getter
@Setter
@NoArgsConstructor
@Dto(forType = "com.bachlinh.order.entity.model.BatchReport")
public class BatchReportResp {

    @MappedDtoField(targetField = "id.toString", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "batchName", outputJsonField = "batch_name")
    private String batchName;

    @MappedDtoField(targetField = "hasError", outputJsonField = "is_error")
    private boolean hasError;

    @MappedDtoField(targetField = "errorDetail", outputJsonField = "error_detail")
    private String errorDetail;

    @MappedDtoField(targetField = "timeReport.toString", outputJsonField = "time_report")
    private String timeReport;
}
