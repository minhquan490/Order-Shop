package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

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

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setValue(String value) {
        if (this.value != null && !this.value.equals(value)) {
            trackUpdatedField("VALUE", this.value);
        }
        this.value = value;
    }

    public static EntityMapper<MessageSetting> getMapper() {
        return new MessageSettingMapper();
    }

    private static class MessageSettingMapper implements EntityMapper<MessageSetting> {

        @Override
        public MessageSetting map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new MessageSetting().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public MessageSetting map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            MessageSetting result = new MessageSetting();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("MESSAGE_SETTING")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            return result;
        }

        private void setData(MessageSetting target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "MESSAGE_SETTING.ID" -> target.setId(mappingObject.value());
                case "MESSAGE_SETTING.VALUE" -> target.setValue((String) mappingObject.value());
                case "MESSAGE_SETTING.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "MESSAGE_SETTING.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "MESSAGE_SETTING.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "MESSAGE_SETTING.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
