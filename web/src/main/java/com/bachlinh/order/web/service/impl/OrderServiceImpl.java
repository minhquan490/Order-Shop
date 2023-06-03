package com.bachlinh.order.web.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.enums.OrderStatusValue;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.OrderRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.service.AbstractService;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.OrderChangeStatusForm;
import com.bachlinh.order.web.dto.form.OrderProductForm;
import com.bachlinh.order.web.dto.resp.OrderInDateResp;
import com.bachlinh.order.web.dto.resp.OrderResp;
import com.bachlinh.order.web.service.business.OrderChangeStatusService;
import com.bachlinh.order.web.service.business.OrderInDateService;
import com.bachlinh.order.web.service.common.OrderService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ServiceComponent
@ActiveReflection
@Slf4j
public class OrderServiceImpl extends AbstractService<OrderResp, OrderProductForm> implements OrderService, OrderChangeStatusService, OrderInDateService {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private EntityFactory entityFactory;


    @ActiveReflection
    @DependenciesInitialize
    public OrderServiceImpl(ThreadPoolTaskExecutor executor, ContainerWrapper wrapper, @Value("${active.profile}") String profile) {
        super(executor, wrapper, profile);
    }

    @Override
    protected OrderResp doSave(OrderProductForm param) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> productQueryCondition = new HashMap<>(1);
        productQueryCondition.put("IDS", param.details().stream().map(OrderProductForm.OrderDetailForm::productId).toList());
        Map<String, Product> productMap = productRepository.getProductsByCondition(productQueryCondition, Pageable.unpaged())
                .stream()
                .collect(Collectors.toMap(Product::getId, value -> value));
        Order order = entityFactory.getEntity(Order.class);
        order.setTimeOrder(Timestamp.from(Instant.now()));
        order.setDeposited(false);
        order.setTotalDeposit(0);
        order.setCustomer(customer);
        Collection<OrderProductForm.OrderDetailForm> orderDetail = param.details();
        Collection<OrderDetail> orderDetails = orderDetail
                .stream()
                .map(request -> {
                    OrderDetail detail = entityFactory.getEntity(OrderDetail.class);
                    detail.setOrder(order);
                    detail.setAmount(request.amount());
                    detail.setProduct(productMap.get(request.productId()));
                    return detail;
                })
                .toList();
        order.setOrderDetails(orderDetails);
        OrderStatus status = entityFactory.getEntity(OrderStatus.class);
        status.setOrder(order);
        status.setStatus(OrderStatusValue.UN_CONFIRMED.name());
        order.setOrderStatus(status);
        Order savedOrder = orderRepository.saveOrder(order);
        return new OrderResp(savedOrder.getId(),
                savedOrder.getTimeOrder().toString(),
                status.getStatus(),
                orderDetails.stream()
                        .map(o -> new OrderResp.OrderDetailResp(String.valueOf(o.getAmount()), o.getProduct().getId(), o.getProduct().getName()))
                        .toList()
                        .toArray(new OrderResp.OrderDetailResp[0]));
    }

    @Override
    protected OrderResp doUpdate(OrderProductForm param) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.getOrder(param.orderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order with id [" + param.orderId() + "] not found", "");
        }
        OrderStatusValue orderStatusValue;
        try {
            orderStatusValue = OrderStatusValue.valueOf(param.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadVariableException("Unknown order status");
        }
        order.getOrderStatus().setStatus(orderStatusValue.name());
        orderRepository.updateOrder(order);

        if (orderStatusValue.equals(OrderStatusValue.CONFIRMED)) {
            EntitySavePointManager savePointManager = entityFactory.getTransactionManager().getSavePointManager();
            savePointManager.createSavePoint("customerOrderPoint");
            AtomicInteger atomicInteger = new AtomicInteger(0);
            order.getOrderDetails().forEach(detail -> atomicInteger.set(atomicInteger.get() + (detail.getAmount() * detail.getProduct().getOrderPoint())));
            customer.setOrderPoint(customer.getOrderPoint() + atomicInteger.get());
            try {
                customerRepository.updateCustomer(customer);
            } catch (Exception e) {
                log.error("Update customer order point failure", e);
                savePointManager.rollbackToSavePoint("customerOrderPoint");
            }
        }
        return new OrderResp(order.getId(),
                order.getTimeOrder().toString(),
                order.getOrderStatus().getStatus(),
                order.getOrderDetails()
                        .stream()
                        .map(o -> new OrderResp.OrderDetailResp(String.valueOf(o.getAmount()), o.getProduct().getId(), o.getProduct().getName()))
                        .toList()
                        .toArray(new OrderResp.OrderDetailResp[0]));
    }

    @Override
    protected OrderResp doDelete(OrderProductForm param) {
        Order order = orderRepository.getOrder(param.orderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order with id [" + param.orderId() + "] not found", "");
        }
        orderRepository.deleteOrder(order);
        return null;
    }

    @Override
    protected OrderResp doGetOne(OrderProductForm param) {
        Order order = orderRepository.getOrder(param.orderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order with id [" + param.orderId() + "] did not existed", "");
        }
        return new OrderResp(order.getId(),
                order.getTimeOrder().toString(),
                order.getOrderStatus().getStatus(),
                order.getOrderDetails()
                        .stream()
                        .map(orderDetail -> new OrderResp.OrderDetailResp(String.valueOf(orderDetail.getAmount()), orderDetail.getProduct().getId(), orderDetail.getProduct().getName()))
                        .toList()
                        .toArray(new OrderResp.OrderDetailResp[0]));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <K, X extends Iterable<K>> X doGetList(OrderProductForm param) {
        Customer customer = customerRepository.getCustomerById(param.customerId(), false);
        return (X) orderRepository.getOrderOfCustomer(customer)
                .stream()
                .map(order -> new OrderResp(order.getId(),
                        order.getTimeOrder().toString(),
                        order.getOrderStatus().getStatus(),
                        order.getOrderDetails()
                                .stream()
                                .map(orderDetail -> new OrderResp.OrderDetailResp(String.valueOf(orderDetail.getAmount()), orderDetail.getProduct().getId(), orderDetail.getProduct().getName()))
                                .toList()
                                .toArray(new OrderResp.OrderDetailResp[0])))
                .toList();
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (orderRepository == null) {
            orderRepository = resolver.resolveDependencies(OrderRepository.class);
        }
        if (productRepository == null) {
            productRepository = resolver.resolveDependencies(ProductRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = resolver.resolveDependencies(CustomerRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolver.resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public void updateOrderStatus(OrderChangeStatusForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.getOrder(form.orderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order with id [" + form.orderId() + "] not found", "");
        }
        OrderStatusValue orderStatusValue;
        try {
            orderStatusValue = OrderStatusValue.valueOf(form.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadVariableException("Unknown order status");
        }
        order.getOrderStatus().setStatus(orderStatusValue.name());
        orderRepository.updateOrder(order);

        if (orderStatusValue.equals(OrderStatusValue.CONFIRMED)) {
            EntitySavePointManager savePointManager = entityFactory.getTransactionManager().getSavePointManager();
            savePointManager.createSavePoint("customerOrderPoint");
            AtomicInteger atomicInteger = new AtomicInteger(0);
            order.getOrderDetails().forEach(detail -> atomicInteger.set(atomicInteger.get() + (detail.getAmount() * detail.getProduct().getOrderPoint())));
            customer.setOrderPoint(customer.getOrderPoint() + atomicInteger.get());
            try {
                customerRepository.updateCustomer(customer);
            } catch (Exception e) {
                log.error("Update customer order point failure", e);
                savePointManager.rollbackToSavePoint("customerOrderPoint");
            }
        }
    }

    @Override
    public Page<OrderInDateResp> getOrdersInDate() {
        List<Order> orders = orderRepository.getNewOrdersInDate();
        List<OrderInDateResp> resp = orders.stream()
                .map(order -> new OrderInDateResp(order.getId(), String.valueOf(order.isDeposited()), String.valueOf(order.getTotalDeposit()), order.getTimeOrder().toString()))
                .toList();
        return new PageImpl<>(resp);
    }

    @Override
    public int numberOrderInDate() {
        return (int) getOrdersInDate().getTotalElements();
    }
}
