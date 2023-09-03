package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Entity
@Table(name = "ORDER_STATUS")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
public class OrderStatus extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @OneToOne(optional = false, mappedBy = "orderStatus", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    private Order order;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderStatus must be int");
        }
        this.id = (Integer) id;
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
    public void setStatus(String status) {
        if (this.status != null && !this.status.equals(status)) {
            trackUpdatedField("STATUS", this.status, status);
        }
        this.status = status;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }

    public static EntityMapper<OrderStatus> getMapper() {
        return new OrderStatusMapper();
    }

    private static class OrderStatusMapper implements EntityMapper<OrderStatus> {

        @Override
        public OrderStatus map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new OrderStatus().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public OrderStatus map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            OrderStatus result = new OrderStatus();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ORDER_STATUS")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Order.getMapper();
                if (mapper.canMap(resultSet)) {
                    var order = mapper.map(resultSet);
                    order.setOrderStatus(result);
                    result.setOrder(order);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("ORDER_STATUS");
            });
        }

        private void setData(OrderStatus target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "ORDER_STATUS.ID" -> target.setId(mappingObject.value());
                case "ORDER_STATUS.STATUS" -> target.setStatus((String) mappingObject.value());
                case "ORDER_STATUS.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "ORDER_STATUS.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "ORDER_STATUS.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "ORDER_STATUS.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}