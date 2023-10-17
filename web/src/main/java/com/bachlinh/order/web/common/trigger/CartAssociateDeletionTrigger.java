package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.web.repository.spi.CartDetailRepository;
import com.bachlinh.order.web.repository.spi.ProductCartRepository;

import java.util.Collection;

@ApplyOn(entity = Cart.class)
@ActiveReflection
public class CartAssociateDeletionTrigger extends AbstractRepositoryTrigger<Cart> {

    private CartDetailRepository cartDetailRepository;
    private ProductCartRepository productCartRepository;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_DELETE};
    }

    @Override
    public String getTriggerName() {
        return "CartAssociateDeletion";
    }

    @Override
    protected void doExecute(Cart entity) {
        Collection<CartDetail> cartDetails = cartDetailRepository.getCartDetailsOfCart(entity);
        cartDetailRepository.deleteCartDetails(cartDetails);

        productCartRepository.deleteProductCart(entity);
    }

    @Override
    protected void inject() {
        if (cartDetailRepository == null) {
            cartDetailRepository = resolveRepository(CartDetailRepository.class);
        }
        if (productCartRepository == null) {
            productCartRepository = resolveRepository(ProductCartRepository.class);
        }
    }
}
