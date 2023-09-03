package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Entity
@Table(name = "BATCH_REPORT")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@EqualsAndHashCode(callSuper = true)
@Getter
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
    @ActiveReflection
    public void setId(@NonNull Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
        }
        throw new PersistenceException("Id of BatchReport must be int");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setBatchName(@NonNull String batchName) {
        if (this.batchName != null && this.batchName.equals(batchName)) {
            trackUpdatedField("BATCH_NAME", this.batchName, batchName);
        }
        this.batchName = batchName;
    }

    @ActiveReflection
    public void setHasError(boolean hasError) {
        if (this.hasError != hasError) {
            trackUpdatedField("HAS_ERROR", this.hasError, hasError);
        }
        this.hasError = hasError;
    }

    @ActiveReflection
    public void setErrorDetail(@NonNull String errorDetail) {
        if (this.errorDetail != null && this.errorDetail.equals(errorDetail)) {
            trackUpdatedField("ERROR_DETAIL", this.errorDetail, errorDetail);
        }
        this.errorDetail = errorDetail;
    }

    @ActiveReflection
    public void setTimeReport(@NonNull Timestamp timeReport) {
        if (this.timeReport != null && this.timeReport.equals(timeReport)) {
            trackUpdatedField("TIME_REPORT", this.timeReport, timeReport);
        }
        this.timeReport = timeReport;
    }

    public static EntityMapper<BatchReport> getMapper() {
        return new BatchReportMapper();
    }

    private static class BatchReportMapper implements EntityMapper<BatchReport> {

        @Override
        public BatchReport map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new BatchReport().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public BatchReport map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            BatchReport result = new BatchReport();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("BATCH_REPORT")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("BATCH_REPORT");
            });
        }

        private void setData(BatchReport target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "BATCH_REPORT.ID" -> target.setId(mappingObject.value());
                case "BATCH_REPORT.BATCH_NAME" -> target.setBatchName((String) mappingObject.value());
                case "BATCH_REPORT.HAS_ERROR" -> target.setHasError((Boolean) mappingObject.value());
                case "BATCH_REPORT.ERROR_DETAIL" -> target.setErrorDetail((String) mappingObject.value());
                case "BATCH_REPORT.TIME_REPORT" -> target.setTimeReport((Timestamp) mappingObject.value());
                case "BATCH_REPORT.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "BATCH_REPORT.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "BATCH_REPORT.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "BATCH_REPORT.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
