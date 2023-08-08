package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "MESSAGE_SETTING", indexes = @Index(name = "idx_message_setting_value", columnList = "VALUE"))
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@Getter
@ActiveReflection
@Label("MSG-")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "messageSetting")
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class MessageSetting extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "VALUE", nullable = false, columnDefinition = "varchar(200)", unique = true)
    private String value;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of message setting must be string");
    }

    @ActiveReflection
    public void setValue(String value) {
        if (this.value != null && !this.value.equals(value)) {
            trackUpdatedField("VALUE", this.value);
        }
        this.value = value;
    }
}
