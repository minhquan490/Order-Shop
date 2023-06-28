package com.bachlinh.order.web.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Cart;
import com.bachlinh.order.entity.model.CartDetail;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.repository.CartRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.form.CartForm;
import com.bachlinh.order.web.dto.resp.CartResp;
import com.bachlinh.order.web.service.common.CartService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@ServiceComponent
@ActiveReflection
public class CartServiceImpl implements CartService {
    private static final Pattern QUOTE_PATTERN = Pattern.compile("'(''|[^'])*'");
    private static final Pattern STATEMENT_PATTERN = Pattern.compile("\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|UNION( +ALL){0,1})\\b");
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final EntityFactory entityFactory;

    @DependenciesInitialize
    @ActiveReflection
    public CartServiceImpl(ProductRepository productRepository, CartRepository cartRepository, EntityFactory entityFactory) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.entityFactory = entityFactory;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResp updateCart(CartForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartRepository.getCart(customer);
        Collection<String> productIds = Stream.of(form.getProductForms()).map(CartForm.ProductForm::id).toList();
        List<Product> products = productRepository.getProductsWithUnion(productIds, Collections.emptyMap(), Pageable.unpaged()).toList();
        for (var f : form.getProductForms()) {
            String id = f.id();
            if (QUOTE_PATTERN.matcher(id).matches()) {
                id = id.replace(QUOTE_PATTERN.pattern(), "");
            }
            if (STATEMENT_PATTERN.matcher(id).matches()) {
                id = id.replace(STATEMENT_PATTERN.pattern(), "");
            }
            Product product = this.searchProduct(products, id);
            if (product == null) {
                throw new ResourceNotFoundException("Product has name [" + f.name() + "] did not existed", "");
            }
            CartDetail cartDetail = entityFactory.getEntity(CartDetail.class);
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            if (!ValidateUtils.isNumber(f.amount())) {
                throw new BadVariableException("Amount should be int");
            }
            int amount = Integer.parseInt(f.amount());
            cartDetail.setAmount(amount);
            cart.getCartDetails().add(cartDetail);
        }
        cartRepository.updateCart(cart);
        return new CartResp(200, "Add to cart success");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResp removeProductFromCart(CartForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartRepository.getCart(customer);
        Collection<String> productIds = Stream.of(form.getProductForms()).map(CartForm.ProductForm::id).toList();
        boolean injection = productIds.stream().anyMatch(s -> QUOTE_PATTERN.matcher(s).matches() || STATEMENT_PATTERN.matcher(s).matches());
        if (injection) {
            throw new BadVariableException("One product is invalid");
        }
        List<Product> products = productRepository.getProductsWithUnion(productIds, Collections.emptyMap(), Pageable.unpaged()).toList();
        List<CartDetail> cartDetails = new ArrayList<>(cart.getCartDetails());
        for (Product product : products) {
            int index = searchCartDetail(cartDetails, product.getId());
            cartDetails.remove(index);
        }
        cart.setCartDetails(cartDetails);
        cartRepository.updateCart(cart);
        return new CartResp(200, "OK");
    }

    private Product searchProduct(List<Product> list, String id) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            var midTarget = list.get(mid);
            int cmp = midTarget.getId().compareTo(id);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return midTarget;
        }
        return null;
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
