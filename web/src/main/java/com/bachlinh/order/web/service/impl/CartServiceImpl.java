package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.CartDetailRepository;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.web.dto.form.customer.CartDetailRemoveForm;
import com.bachlinh.order.web.dto.form.customer.CartForm;
import com.bachlinh.order.web.dto.resp.CartResp;
import com.bachlinh.order.web.service.common.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@ServiceComponent
@ActiveReflection
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    @ActiveReflection
    public CartServiceImpl(CartRepository cartRepository, CartDetailRepository cartDetailRepository) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResp updateCart(CartForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<CartForm.ProductForm> productForms = List.of(form.getProductForms());
        Cart cart = cartRepository.getCartForUpdateCartDetail(customer, productForms.stream().map(CartForm.ProductForm::id).toList());
        var cartDetails = new ArrayList<>(cart.getCartDetails());
        Collection<CartDetail> updatedCartDetails = new LinkedList<>();
        productForms.forEach(s -> {
            int cartDetailPosition = searchCartDetail(cartDetails, s.id());
            if (cartDetailPosition >= 0) {
                var cartDetail = cartDetails.get(cartDetailPosition);
                cartDetail.setAmount(Integer.parseInt(s.amount()));
                updatedCartDetails.add(cartDetail);
            }
        });
        cartDetailRepository.updateCartDetails(updatedCartDetails);
        return new CartResp(200, "Add to cart success");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResp removeProductFromCart(CartDetailRemoveForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var cartDetailIds = form.getCartDetailIds();
        Cart cart = cartRepository.getCartForDeleteCartDetail(customer, List.of(cartDetailIds));
        cartDetailRepository.deleteCartDetails(cart.getCartDetails());
        return new CartResp(200, "OK");
    }

    private int searchCartDetail(List<CartDetail> list, String productId) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            var midTarget = list.get(mid);
            int cmp = midTarget.getProduct().getId().compareTo(productId);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid;
        }
        return -1;
    }
}
