package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CartRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Repository
@Primary
class CartRepositoryImpl extends AbstractRepository<Cart, String> implements CartRepository {

    @Autowired
    CartRepositoryImpl(ApplicationContext applicationContext) {
        super(Cart.class, applicationContext);
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
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
