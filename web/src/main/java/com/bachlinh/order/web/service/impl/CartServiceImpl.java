package com.bachlinh.order.web.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.bachlinh.order.service.AbstractService;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesResolver;
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
public class CartServiceImpl extends AbstractService<CartResp, CartForm> implements CartService {
    private static final Pattern QUOTE_PATTERN = Pattern.compile("'(''|[^'])*'");
    private static final Pattern STATEMENT_PATTERN = Pattern.compile("\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE){0,1}|INSERT( +INTO){0,1}|MERGE|SELECT|UPDATE|UNION( +ALL){0,1})\\b");
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private EntityFactory entityFactory;

    @DependenciesInitialize
    @ActiveReflection
    CartServiceImpl(ThreadPoolTaskExecutor executor, ContainerWrapper wrapper, @Value("${active.profile}") String profile) {
        super(executor, wrapper, profile);
    }

    @Override
    protected CartResp doSave(CartForm param) {
        // Return psudo object
        return new CartResp(-1, "");
    }

    @Override
    protected CartResp doUpdate(CartForm param) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartRepository.getCart(customer);
        Collection<String> productIds = Stream.of(param.getProductForms()).map(CartForm.ProductForm::id).toList();
        List<Product> products = productRepository.getProductsWithUnion(productIds, Collections.emptyMap(), Pageable.unpaged()).toList();
        for (var form : param.getProductForms()) {
            String id = form.id();
            if (QUOTE_PATTERN.matcher(id).matches()) {
                id = id.replace(QUOTE_PATTERN.pattern(), "");
            }
            if (STATEMENT_PATTERN.matcher(id).matches()) {
                id = id.replace(STATEMENT_PATTERN.pattern(), "");
            }
            Product product = this.searchProduct(products, id);
            if (product == null) {
                throw new ResourceNotFoundException("Product has name [" + form.name() + "] did not existed");
            }
            CartDetail cartDetail = entityFactory.getEntity(CartDetail.class);
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            int amount;
            try {
                amount = Integer.parseInt(form.amount());
            } catch (NumberFormatException e) {
                throw new BadVariableException("Amount should be int");
            }
            cartDetail.setAmount(amount);
            cart.getCartDetails().add(cartDetail);
        }
        cartRepository.updateCart(cart);
        return new CartResp(200, "Add to cart success");
    }

    @Override
    protected CartResp doDelete(CartForm param) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = cartRepository.getCart(customer);
        Collection<String> productIds = Stream.of(param.getProductForms()).map(CartForm.ProductForm::id).toList();
        boolean injection = productIds.stream().anyMatch(s -> QUOTE_PATTERN.matcher(s).matches() || STATEMENT_PATTERN.matcher(s).matches());
        if (injection) {
            throw new BadVariableException("One element in product id array is invalid");
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

    @Override
    protected CartResp doGetOne(CartForm param) {
        return new CartResp(-1, "");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K, X extends Iterable<K>> X doGetList(CartForm param) {
        return (X) new PageImpl<>(List.of(new CartResp(-1, "")));
    }


    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (productRepository == null) {
            productRepository = resolver.resolveDependencies(ProductRepository.class);
        }
        if (cartRepository == null) {
            cartRepository = resolver.resolveDependencies(CartRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolver.resolveDependencies(EntityFactory.class);
        }
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
