package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import com.bachlinh.order.annotation.ActiveReflection;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "CRAWL_RESULT")
@ActiveReflection
public class CrawlResult extends AbstractEntity {

    @Id
    @Column(name = "ID", unique = true, updatable = false)
    private Integer id;

    @Column(name = "SOURCE_PATH", columnDefinition = "nvarchar(100)", updatable = false)
    private String sourcePath;

    @Column(name = "TIME_FINISH", updatable = false)
    private Timestamp timeFinish = Timestamp.from(Instant.now());

    @Column(name = "RESOURCES", columnDefinition = "nvarchar(1000)", updatable = false)
    private String resources;

    // @formatter:off
    @ActiveReflection
    CrawlResult() {}
    // @formatter:on

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
        }
        throw new PersistenceException("Id of CrawlResult must be int");
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Timestamp getTimeFinish() {
        return timeFinish;
    }

    public void setTimeFinish(Timestamp timeFinish) {
        this.timeFinish = timeFinish;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrawlResult that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
