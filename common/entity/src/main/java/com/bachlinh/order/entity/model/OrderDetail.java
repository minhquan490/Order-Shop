package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

@Entity
@Table(name = "ORDER_DETAIL", indexes = @Index(name = "idx_order", columnList = "ORDER_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class OrderDetail extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ORDER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Order order;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderDetail must be int");
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
    public void setAmount(Integer amount) {
        if (this.amount != null && !this.amount.equals(amount)) {
            trackUpdatedField("AMOUNT", this.amount.toString());
        }
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        if (this.product != null && !Objects.requireNonNull(this.product.getId()).equals(product.getId())) {
            trackUpdatedField("PRODUCT_ID", product.getId());
        }
        this.product = product;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        if (this.order != null && !Objects.requireNonNull(this.order.getId()).equals(order.getId())) {
            trackUpdatedField("ORDER_ID", this.order.getId());
        }
        this.order = order;
    }

    public static EntityMapper<OrderDetail> getMapper() {
        return new OrderDetailMapper();
    }

    private static class OrderDetailMapper implements EntityMapper<OrderDetail> {

        @Override
        public OrderDetail map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new OrderDetail().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public OrderDetail map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            OrderDetail result = new OrderDetail();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ORDER_DETAIL")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Product.getMapper();
                if (mapper.canMap(resultSet)) {
                    var product = mapper.map(resultSet);
                    result.setProduct(product);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Order.getMapper();
                if (mapper.canMap(resultSet)) {
                    var order = mapper.map(resultSet);
                    order.getOrderDetails().add(result);
                    result.setOrder(order);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("ORDER_DETAIL");
            });
        }

        private void setData(OrderDetail target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "ORDER_DETAIL.ID" -> target.setId(mappingObject.value());
                case "ORDER_DETAIL.AMOUNT" -> target.setAmount((Integer) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
