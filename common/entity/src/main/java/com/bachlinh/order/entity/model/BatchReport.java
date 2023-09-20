package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BatchReport other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$batchName = this.getBatchName();
        final Object other$batchName = other.getBatchName();
        if (!Objects.equals(this$batchName, other$batchName)) return false;
        final Object this$hasError = this.getHasError();
        final Object other$hasError = other.getHasError();
        if (!Objects.equals(this$hasError, other$hasError)) return false;
        final Object this$errorDetail = this.getErrorDetail();
        final Object other$errorDetail = other.getErrorDetail();
        if (!Objects.equals(this$errorDetail, other$errorDetail))
            return false;
        final Object this$timeReport = this.getTimeReport();
        final Object other$timeReport = other.getTimeReport();
        return Objects.equals(this$timeReport, other$timeReport);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BatchReport;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $batchName = this.getBatchName();
        result = result * PRIME + ($batchName == null ? 43 : $batchName.hashCode());
        final Object $hasError = this.getHasError();
        result = result * PRIME + ($hasError == null ? 43 : $hasError.hashCode());
        final Object $errorDetail = this.getErrorDetail();
        result = result * PRIME + ($errorDetail == null ? 43 : $errorDetail.hashCode());
        final Object $timeReport = this.getTimeReport();
        result = result * PRIME + ($timeReport == null ? 43 : $timeReport.hashCode());
        return result;
    }
}
