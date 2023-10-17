package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.core.annotation.QueryCache;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.util.Collection;

@Table(name = "MESSAGE_SETTING", indexes = @Index(name = "idx_message_setting_value", columnList = "VALUE"))
@Entity
@ActiveReflection
@Label("MSG-")
@QueryCache
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class MessageSetting extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "VALUE", nullable = false, columnDefinition = "varchar(200)", unique = true)
    private String value;

    @ActiveReflection
    protected MessageSetting() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of message setting must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setValue(String value) {
        if (this.value != null && !this.value.equals(value)) {
            trackUpdatedField("VALUE", this.value, value);
        }
        this.value = value;
    }

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageSetting that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getValue());
    }
}
