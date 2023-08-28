package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CartDetailRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class CartDetailRepositoryImpl extends AbstractRepository<CartDetail, Integer> implements CartDetailRepository {

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
}
