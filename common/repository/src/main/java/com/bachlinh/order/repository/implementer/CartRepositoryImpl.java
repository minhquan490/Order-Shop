package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail_;
import com.bachlinh.order.entity.model.Cart_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.JoinOperation;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelection;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.query.WhereOperation;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class CartRepositoryImpl extends AbstractRepository<Cart, String> implements CartRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CartRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Cart.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Cart saveCart(Cart cart) {
        return Optional.of(this.save(cart)).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
    public Cart updateCart(Cart cart) {
        return saveCart(cart);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = READ_COMMITTED)
    public void deleteCart(Cart cart) {
        delete(cart);
    }

    @Override
    public Cart getCart(Customer customer) {
        var cartDetailsJoin = Join.builder().attribute(Cart_.CART_DETAILS).type(JoinType.INNER).build();
        var productJoin = Join.builder().attribute(CartDetail_.PRODUCT).type(JoinType.INNER).build();
        var orderBy = OrderBy.builder().column(CartDetail_.PRODUCT).type(OrderBy.Type.ASC).build();
        var customerWhere = Where.builder().attribute(Cart_.CUSTOMER).value(customer).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(Cart.class);
        JoinOperation joinOperation = sqlSelection.join(cartDetailsJoin).join(productJoin);
        joinOperation.orderBy(orderBy);
        WhereOperation whereOperation = joinOperation.where(customerWhere);
        String query = whereOperation.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>();
        whereOperation.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        var results = executeNativeQuery(query, attributes, Cart.class);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
