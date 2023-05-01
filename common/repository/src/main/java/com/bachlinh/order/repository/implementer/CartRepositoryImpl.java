package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail_;
import com.bachlinh.order.entity.model.Cart_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Repository
@ActiveReflection
class CartRepositoryImpl extends AbstractRepository<Cart, String> implements CartRepository {

    @Autowired
    @ActiveReflection
    CartRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Cart.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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
        Specification<Cart> spec = Specification.where((root, query, criteriaBuilder) -> {
            var join = root.join(Cart_.cartDetails, JoinType.INNER).join(CartDetail_.product, JoinType.INNER);
            query.orderBy(criteriaBuilder.asc(join.get(CartDetail_.PRODUCT)));
            return criteriaBuilder.equal(root.get(Cart_.customer), customer);
        });
        return findOne(spec).orElse(null);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
