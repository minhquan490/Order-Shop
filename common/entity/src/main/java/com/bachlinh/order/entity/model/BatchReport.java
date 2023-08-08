package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;

@Entity
@Table(name = "BATCH_REPORT")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class BatchReport extends AbstractEntity<Integer> {

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
    public void setId(@NonNull Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
        }
        throw new PersistenceException("Id of BatchReport must be int");
    }

    @ActiveReflection
    public void setBatchName(@NonNull String batchName) {
        if (this.batchName != null && this.batchName.equals(batchName)) {
            trackUpdatedField("BATCH_NAME", this.batchName);
        }
        this.batchName = batchName;
    }

    @ActiveReflection
    public void setHasError(boolean hasError) {
        if (this.hasError != hasError) {
            trackUpdatedField("HAS_ERROR", String.valueOf(this.hasError));
        }
        this.hasError = hasError;
    }

    @ActiveReflection
    public void setErrorDetail(@NonNull String errorDetail) {
        if (this.errorDetail != null && this.errorDetail.equals(errorDetail)) {
            trackUpdatedField("ERROR_DETAIL", this.errorDetail);
        }
        this.errorDetail = errorDetail;
    }

    @ActiveReflection
    public void setTimeReport(@NonNull Timestamp timeReport) {
        if (this.timeReport != null && this.timeReport.equals(timeReport)) {
            trackUpdatedField("TIME_REPORT", this.timeReport.toString());
        }
        this.timeReport = timeReport;
    }
}
