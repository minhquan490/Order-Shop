package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "BATCH_REPORT")
@ActiveReflection
@Validator(validators = "com.bachlinh.order.validate.validator.internal.BatchReportValidator")
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@Getter
public class BatchReport extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "BATCH_NAME", nullable = false, length = 100, updatable = false)
    private String batchName;

    @Column(name = "HAS_ERROR", nullable = false, columnDefinition = "bit", updatable = false)
    private boolean hasError = false;

    @Column(name = "ERROR_DETAIL", length = 500, updatable = false)
    private String errorDetail;

    @Column(name = "TIME_REPORT", nullable = false, updatable = false)
    private Timestamp timeReport;

    @Override
    public Integer getId() {
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

    @ActiveReflection
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    @ActiveReflection
    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    @ActiveReflection
    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    @ActiveReflection
    public void setTimeReport(Timestamp timeReport) {
        this.timeReport = timeReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchReport that)) return false;
        return isHasError() == that.isHasError() && Objects.equal(getId(), that.getId()) && Objects.equal(getBatchName(), that.getBatchName()) && Objects.equal(getErrorDetail(), that.getErrorDetail()) && Objects.equal(getTimeReport(), that.getTimeReport());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getBatchName(), isHasError(), getErrorDetail(), getTimeReport());
    }
}
