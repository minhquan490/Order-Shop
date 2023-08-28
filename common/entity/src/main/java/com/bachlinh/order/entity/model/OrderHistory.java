package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Queue;

@Entity
@Table(name = "ORDER_HISTORY")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class OrderHistory extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "ORDER_TIME", updatable = false, nullable = false)
    private Timestamp orderTime;

    @Column(name = "ORDER_STATUS", nullable = false, length = 30)
    private String orderStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ORDER_ID")
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Order order;

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (id instanceof Integer) {
            this.id = (int) id;
            return;
        }
        throw new PersistenceException("Can not set id for order history, supported only integer");
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
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @ActiveReflection
    public Order getOrder() {
        return this.order;
    }

    @ActiveReflection
    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }

    public static EntityMapper<OrderHistory> getMapper() {
        return new OrderHistoryMapper();
    }

    private static class OrderHistoryMapper implements EntityMapper<OrderHistory> {

        @Override
        public OrderHistory map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new OrderHistory().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public OrderHistory map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            OrderHistory result = new OrderHistory();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ORDER_HISTORY")) {
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
                    order.setOrderHistory(result);
                    result.setOrder(order);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("ORDER_HISTORY");
            });
        }

        private void setData(OrderHistory target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "ORDER_HISTORY.ID" -> target.setId(mappingObject.value());
                case "ORDER_HISTORY.ORDER_TIME" -> target.setOrderTime((Timestamp) mappingObject.value());
                case "ORDER_HISTORY.ORDER_STATUS" -> target.setOrderStatus((String) mappingObject.value());
                case "ORDER_HISTORY.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "ORDER_HISTORY.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "ORDER_HISTORY.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "ORDER_HISTORY.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}