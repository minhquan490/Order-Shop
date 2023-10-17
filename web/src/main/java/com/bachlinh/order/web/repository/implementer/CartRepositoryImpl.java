package com.bachlinh.order.web.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.JoinType;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.function.VoidCallback;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.CartDetail_;
import com.bachlinh.order.entity.model.Cart_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.CartRepository;
import com.bachlinh.order.web.repository.spi.ProductCartRepository;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CartRepositoryImpl extends AbstractRepository<String, Cart> implements CartRepository, ProductCartRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public CartRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Cart.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public void saveCart(Cart cart) {
        this.save(cart);
    }

    @Override
    public Cart getCartForUpdateCartDetail(Customer owner, Collection<String> productIds) {
        Select cartIdSelect = Select.builder().column(Cart_.ID).build();
        Select cartDetailIdSelect = Select.builder().column(CartDetail_.ID).build();
        Select cartDetailAmountSelect = Select.builder().column(CartDetail_.AMOUNT).build();
        Select cartDetailProductIdSelect = Select.builder().column(Product_.ID).build();
        Join cartDetailJoin = Join.builder().attribute(Cart_.CART_DETAILS).type(JoinType.LEFT).build();
        Join cartDetailProductJoin = Join.builder().attribute(CartDetail_.PRODUCT).type(JoinType.INNER).build();
        Where ownerWhere = Where.builder().attribute(Cart_.CUSTOMER).value(owner).operation(Operation.EQ).build();
        Where productIdsWhere = Where.builder().attribute(CartDetail_.PRODUCT).value(productIds).operation(Operation.IN).build();
        OrderBy productIdOrderBy = OrderBy.builder().column(CartDetail_.PRODUCT).type(OrderBy.Type.ASC).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        sqlSelect.select(cartIdSelect)
                .select(cartDetailIdSelect, CartDetail.class)
                .select(cartDetailAmountSelect, CartDetail.class)
                .select(cartDetailProductIdSelect, Product.class);
        SqlJoin sqlJoin = sqlSelect.join(cartDetailJoin).join(cartDetailProductJoin, Product.class);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere).and(productIdsWhere, CartDetail.class).orderBy(productIdOrderBy);
        return getCart(sqlWhere);
    }

    @Override
    public Cart getCartForDeleteCartDetail(Customer owner, Collection<Integer> cartDetailIds) {
        Select cartIdSelect = Select.builder().column(Cart_.ID).build();
        Select cartDetailIdSelect = Select.builder().column(CartDetail_.ID).build();
        Join cartDetailJoin = Join.builder().attribute(Cart_.CART_DETAILS).type(JoinType.INNER).build();
        Where ownerWhere = Where.builder().attribute(Cart_.CUSTOMER).value(owner).operation(Operation.EQ).build();
        Where cartDetailIdsWhere = Where.builder().attribute(Cart_.CART_DETAILS).value(cartDetailIds).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Cart.class);
        sqlSelect.select(cartIdSelect).select(cartDetailIdSelect, CartDetail.class);
        SqlJoin sqlJoin = sqlSelect.join(cartDetailJoin);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere).and(cartDetailIdsWhere);
        return getCart(sqlWhere);
    }

    @Override
    public Cart getCartOfCustomer(String customerId) {

        Where ownerWhere = Where.builder().attribute(Cart_.CUSTOMER).value(customerId).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);

        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, getDomainClass());
    }

    @Override
    public void deleteCart(Cart cart) {
        if (cart == null) {
            return;
        }
        this.delete(cart);
    }

    private Cart getCart(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, Cart.class);
    }

    @Override
    public void deleteProductCart(Product product) {
        EntityManager entityManager = getEntityManager();

        VoidCallback callback = params -> {
            String sql = "DELETE PRODUCT_CART WHERE PRODUCT_ID = :productId";

            Query query = entityManager.createQuery(sql);
            query.setParameter("productId", product.getId());

            query.executeUpdate();
        };

        doInTransaction(entityManager, callback);
    }

    @Override
    public void deleteProductCart(Cart cart) {
        EntityManager entityManager = getEntityManager();

        VoidCallback callback = params -> {
            String sql = "DELETE PRODUCT_CART WHERE CART_ID = :cartId";

            Query query = entityManager.createQuery(sql);
            query.setParameter("cartId", cart.getId());

            query.executeUpdate();
        };

        doInTransaction(entityManager, callback);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CartRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CartRepository.class, ProductCartRepository.class};
    }
}
