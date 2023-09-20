package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

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

    public BatchReportResp() {
    }

    public String getId() {
        return this.id;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public boolean isHasError() {
        return this.hasError;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public String getTimeReport() {
        return this.timeReport;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public void setTimeReport(String timeReport) {
        this.timeReport = timeReport;
    }
}
