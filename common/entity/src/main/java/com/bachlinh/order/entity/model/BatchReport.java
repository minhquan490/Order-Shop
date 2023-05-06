package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import com.bachlinh.order.annotation.ActiveReflection;

import java.sql.Timestamp;

@Entity
@Table(name = "BATCH_REPORT")
@ActiveReflection
public class BatchReport extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private int id;

    @Column(name = "BATCH_NAME", nullable = false, length = 100, updatable = false)
    private String batchName;

    @Column(name = "HAS_ERROR", nullable = false, columnDefinition = "bit", updatable = false)
    private boolean hasError = false;

    @Column(name = "ERROR_DETAIL", length = 500, updatable = false)
    private String errorDetail;

    @Column(name = "TIME_REPORT", nullable = false, updatable = false)
    private Timestamp timeReport;

    @ActiveReflection
    BatchReport() {
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
        }
        throw new PersistenceException("Id of BatchReport must be int");
    }

    public String getBatchName() {
        return batchName;
    }

    @ActiveReflection
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public boolean isHasError() {
        return hasError;
    }

    @ActiveReflection
    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    @ActiveReflection
    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public Timestamp getTimeReport() {
        return timeReport;
    }

    @ActiveReflection
    public void setTimeReport(Timestamp timeReport) {
        this.timeReport = timeReport;
    }
}
