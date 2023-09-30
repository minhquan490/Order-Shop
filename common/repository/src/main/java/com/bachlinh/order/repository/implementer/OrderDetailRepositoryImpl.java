package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderDetail_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.OrderDetailRepository;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class OrderDetailRepositoryImpl extends AbstractRepository<Integer, OrderDetail> implements OrderDetailRepository, RepositoryBase {

    @ActiveReflection
    @DependenciesInitialize
    public OrderDetailRepositoryImpl(DependenciesResolver resolver) {
        super(OrderDetail.class, resolver);
    }

    @Override
    public Collection<OrderDetail> getOrderDetailsOfOrder(Order order) {
        Where orderWhere = Where.builder().attribute(OrderDetail_.ORDER).value(order).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(OrderDetail.class);
        SqlWhere sqlWhere = sqlSelect.where(orderWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public void deleteOrderDetails(Collection<OrderDetail> orderDetails) {
        deleteAll(orderDetails);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new OrderDetailRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{OrderDetailRepository.class};
    }
}
