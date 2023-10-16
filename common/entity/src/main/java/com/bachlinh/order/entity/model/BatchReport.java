package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import com.bachlinh.order.core.annotation.ActiveReflection;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "BATCH_REPORT")
@ActiveReflection
public class BatchReport extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "BATCH_NAME", nullable = false, length = 100, updatable = false)
    private String batchName;

    @Column(name = "HAS_ERROR", nullable = false, columnDefinition = "bit", updatable = false)
    private Boolean hasError = false;

    @Column(name = "ERROR_DETAIL", length = 500, updatable = false)
    private String errorDetail;

    @Column(name = "TIME_REPORT", nullable = false, updatable = false)
    private Timestamp timeReport;

    @ActiveReflection
    protected BatchReport() {
    }

    public boolean isHasError() {
        return getHasError();
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of BatchReport must be int");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setBatchName(String batchName) {
        if (this.batchName != null && this.batchName.equals(batchName)) {
            trackUpdatedField("BATCH_NAME", this.batchName, batchName);
        }
        this.batchName = batchName;
    }

    @ActiveReflection
    public void setHasError(boolean hasError) {
        if (this.hasError != null && this.hasError != hasError) {
            trackUpdatedField("HAS_ERROR", this.hasError, hasError);
        }
        this.hasError = hasError;
    }

    @ActiveReflection
    public void setErrorDetail(String errorDetail) {
        if (this.errorDetail != null && this.errorDetail.equals(errorDetail)) {
            trackUpdatedField("ERROR_DETAIL", this.errorDetail, errorDetail);
        }
        this.errorDetail = errorDetail;
    }

    @ActiveReflection
    public void setTimeReport(Timestamp timeReport) {
        if (this.timeReport != null && this.timeReport.equals(timeReport)) {
            trackUpdatedField("TIME_REPORT", this.timeReport, timeReport);
        }
        this.timeReport = timeReport;
    }

    public Integer getId() {
        return this.id;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public Boolean getHasError() {
        return this.hasError;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public Timestamp getTimeReport() {
        return this.timeReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchReport that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getBatchName(), that.getBatchName()) && Objects.equal(isHasError(), that.isHasError()) && Objects.equal(getErrorDetail(), that.getErrorDetail()) && Objects.equal(getTimeReport(), that.getTimeReport());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getBatchName(), isHasError(), getErrorDetail(), getTimeReport());
    }
}
