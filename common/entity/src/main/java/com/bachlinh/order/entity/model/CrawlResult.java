package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "CRAWL_RESULT", indexes = @Index(name = "idx_crawl_result_time_finish", columnList = "TIME_FINISH"))
@ActiveReflection
@Validator(validators = "com.bachlinh.order.validate.validator.internal.CrawlResultValidator")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class CrawlResult extends AbstractEntity {

    @Id
    @Column(name = "ID", unique = true, updatable = false)
    private Integer id;

    @Column(name = "SOURCE_PATH", columnDefinition = "nvarchar(100)", updatable = false, nullable = false)
    private String sourcePath;

    @Column(name = "TIME_FINISH", updatable = false, nullable = false)
    private Timestamp timeFinish = Timestamp.from(Instant.now());

    @Column(name = "RESOURCES", columnDefinition = "nvarchar(1000)", updatable = false)
    private String resources;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of CrawlResult must be int");
    }

    @ActiveReflection
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    @ActiveReflection
    public void setTimeFinish(Timestamp timeFinish) {
        this.timeFinish = timeFinish;
    }

    @ActiveReflection
    public void setResources(String resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrawlResult that)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getSourcePath(), that.getSourcePath()) && Objects.equal(getTimeFinish(), that.getTimeFinish()) && Objects.equal(getResources(), that.getResources());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getSourcePath(), getTimeFinish(), getResources());
    }
}
