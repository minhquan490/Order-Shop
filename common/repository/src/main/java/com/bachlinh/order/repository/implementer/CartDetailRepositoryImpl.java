package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.CartDetail_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.CartDetailRepository;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CartDetailRepositoryImpl extends AbstractRepository<Integer, CartDetail> implements CartDetailRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public CartDetailRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(CartDetail.class, dependenciesResolver);
    }

    @Override
    public void updateCartDetails(Collection<CartDetail> cartDetails) {
        saveAll(cartDetails);
    }

    @Override
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

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CartDetailRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CartDetailRepository.class};
    }
}
