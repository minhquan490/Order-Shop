package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.CartDetail_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.CartDetailRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CartDetailRepositoryImpl extends AbstractRepository<Integer, CartDetail> implements CartDetailRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CartDetailRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(CartDetail.class, dependenciesResolver);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void updateCartDetails(Collection<CartDetail> cartDetails) {
        saveAll(cartDetails);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void deleteCartDetails(Collection<CartDetail> cartDetails) {
        var cartDetailIds = cartDetails.stream().map(CartDetail::getId).toList();
        deleteAllById(cartDetailIds);
    }

    @Override
    public Collection<CartDetail> getCartDetailsOfCart(Cart cart) {
        Where cartWhere = Where.builder().attribute(CartDetail_.CART).value(cart).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(cartWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }
}
