package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.model.OrderHistory_;
import com.bachlinh.order.entity.model.Order_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.OrderHistoryRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.QueryExtractor;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesContainerResolver;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class OrderHistoryRepositoryImpl extends AbstractRepository<OrderHistory, Integer> implements OrderHistoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public OrderHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(OrderHistory.class, containerResolver.getDependenciesResolver());
    }

    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    @Override
    public void saveOrderHistory(OrderHistory orderHistory) {
        save(orderHistory);
    }

    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateOrderHistory(OrderHistory orderHistory) {
        saveOrderHistory(orderHistory);
    }

    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteOrderHistory(OrderHistory orderHistory) {
        delete(orderHistory);
    }

    @Override
    public OrderHistory getHistoryByOrder(Order order) {
        Specification<OrderHistory> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(OrderHistory_.order), order));
        return get(spec);
    }

    @Override
    public Collection<OrderHistory> getHistoriesOfCustomer(Customer customer) {
        var orderJoin = Join.builder().attribute(OrderHistory_.ORDER).type(JoinType.INNER).build();
        var customerJoin = Join.builder().attribute(Order_.CUSTOMER).type(JoinType.INNER).build();
        var customerWhere = Where.builder().attribute(Order_.CUSTOMER).value(customer).operator(Operator.EQ).build();
        var orderBy = OrderBy.builder().column(OrderHistory_.ORDER_TIME).type(OrderBy.Type.DESC).build();
        Specification<OrderHistory> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new QueryExtractor(criteriaBuilder, query, root);
            extractor.join(orderJoin, customerJoin);
            extractor.where(customerWhere);
            extractor.orderBy(orderBy);
            return extractor.extract();
        });
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
