package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.CartDetail_;
import com.bachlinh.order.entity.model.Cart_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.Product_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.repository.ProductCartRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class CartRepositoryImpl extends AbstractRepository<String, Cart> implements CartRepository, ProductCartRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CartRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Cart.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteCart(Cart cart) {
        if (cart == null) {
            return;
        }
        this.delete(cart);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    private Cart getCart(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, Cart.class);
    }

    @Override
    public void deleteProductCart(Product product) {
        EntityManager entityManager = getEntityManager();

        TransactionCallback callback = () -> {
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

        TransactionCallback callback = () -> {
            String sql = "DELETE PRODUCT_CART WHERE CART_ID = :cartId";

            Query query = entityManager.createQuery(sql);
            query.setParameter("cartId", cart.getId());

            query.executeUpdate();
        };

        doInTransaction(entityManager, callback);
    }
}
