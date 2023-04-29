package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.Order_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@Primary
class OrderRepositoryImpl extends AbstractRepository<Order, String> implements OrderRepository {

    @Autowired
    OrderRepositoryImpl(ApplicationContext applicationContext) {
        super(Order.class, applicationContext);
    }

    @Override
    @Transactional(propagation = MANDATORY)
    public Order saveOrder(Order order) {
        return Optional.of(this.save(order)).orElse(null);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Order updateOrder(Order order) {
        return saveOrder(order);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteOrder(Order order) {
        if (order == null) {
            return false;
        }
        if (existsById(order.getId())) {
            delete(order);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Order getOrder(String orderId) {
        Specification<Order> spec = Specification.where((root, query, criteriaBuilder) -> {
            root.join(Order_.orderStatus, JoinType.INNER);
            root.join(Order_.orderDetails, JoinType.INNER);
            return criteriaBuilder.equal(root.get(Order_.id), orderId);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public List<Order> getNewOrdersInDate() {
        LocalDate now = LocalDate.now();
        Specification<Order> spec = Specification.where((root, query, criteriaBuilder) -> {
            Predicate firstStatement = criteriaBuilder.greaterThanOrEqualTo(root.get(Order_.timeOrder), Timestamp.valueOf(LocalDateTime.of(now, LocalTime.of(0, 0, 0))));
            Predicate secondStatement = criteriaBuilder.lessThanOrEqualTo(root.get(Order_.timeOrder), Timestamp.valueOf(LocalDateTime.of(now, LocalTime.of(23, 59, 59))));
            return criteriaBuilder.and(firstStatement, secondStatement);
        });
        return findAll(spec, Sort.unsorted());
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
