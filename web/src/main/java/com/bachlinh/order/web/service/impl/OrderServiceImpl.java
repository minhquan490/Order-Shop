package com.bachlinh.order.web.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.OrderStatusValue;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.OrderRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.web.dto.form.OrderChangeStatusForm;
import com.bachlinh.order.web.dto.form.OrderProductForm;
import com.bachlinh.order.web.dto.form.customer.OrderCreateForm;
import com.bachlinh.order.web.dto.resp.OrderInfoResp;
import com.bachlinh.order.web.dto.resp.OrderListResp;
import com.bachlinh.order.web.dto.resp.OrderResp;
import com.bachlinh.order.web.service.business.OrderChangeStatusService;
import com.bachlinh.order.web.service.business.OrderInDateService;
import com.bachlinh.order.web.service.common.OrderService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ServiceComponent
@ActiveReflection
@Slf4j
public class OrderServiceImpl implements OrderService, OrderChangeStatusService, OrderInDateService {
    private static final String SAVE_POINT_NAME = "customerOrderPoint";
    private static final String ERROR_TEMPLATE = "Order with id [%s] not found";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final EntityFactory entityFactory;

    @ActiveReflection
    @DependenciesInitialize
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, CustomerRepository customerRepository, EntityFactory entityFactory) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.entityFactory = entityFactory;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void updateOrderStatus(OrderChangeStatusForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.getOrder(form.orderId());
        if (order == null) {
            throw new ResourceNotFoundException(String.format(ERROR_TEMPLATE, form.orderId()), "");
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
            savePointManager.createSavePoint(SAVE_POINT_NAME);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            order.getOrderDetails().forEach(detail -> atomicInteger.set(atomicInteger.get() + (detail.getAmount() * detail.getProduct().getOrderPoint())));
            customer.setOrderPoint(customer.getOrderPoint() + atomicInteger.get());
            try {
                customerRepository.updateCustomer(customer);
            } catch (Exception e) {
                log.error("Update customer order point failure", e);
                savePointManager.rollbackToSavePoint(SAVE_POINT_NAME);
            }
        }
    }

    @Override
    public Page<OrderListResp> getOrdersInDate() {
        List<Order> orders = orderRepository.getNewOrdersInDate();
        List<OrderListResp> result = orders.stream()
                .map(order -> {
                    var resp = new OrderListResp();
                    resp.setId(order.getId());
                    resp.setOrderStatus(order.getOrderStatus().getStatus());
                    resp.setCustomerName(order.getCustomer().getUsername());
                    resp.setDeposited(String.valueOf(order.getBankTransactionCode().isEmpty()));
                    return resp;
                })
                .toList();
        return new PageImpl<>(result);
    }

    @Override
    public int numberOrderInDate() {
        return (int) getOrdersInDate().getTotalElements();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public OrderResp saveOrder(OrderCreateForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> productQueryCondition = new HashMap<>(1);
        productQueryCondition.put("IDS", Stream.of(form.getDetails()).map(OrderCreateForm.Detail::getProductId).toList());
        Map<String, Product> productMap = productRepository.getProductsByCondition(productQueryCondition, Pageable.unpaged())
                .stream()
                .collect(Collectors.toMap(Product::getId, value -> value));
        Order order = entityFactory.getEntity(Order.class);
        order.setTimeOrder(Timestamp.from(Instant.now()));
        order.setCustomer(customer);
        Collection<OrderCreateForm.Detail> orderDetail = Arrays.asList(form.getDetails());
        Collection<OrderDetail> orderDetails = orderDetail
                .stream()
                .map(request -> {
                    OrderDetail detail = entityFactory.getEntity(OrderDetail.class);
                    detail.setOrder(order);
                    detail.setAmount(Integer.parseInt(request.getAmount()));
                    detail.setProduct(productMap.get(request.getProductId()));
                    return detail;
                })
                .toList();
        order.setOrderDetails(orderDetails);
        order.setBankTransactionCode(form.getBankTransactionCode());
        OrderStatus status = entityFactory.getEntity(OrderStatus.class);
        status.setOrder(order);
        status.setStatus(OrderStatusValue.UN_CONFIRMED.name());
        order.setOrderStatus(status);
        Order savedOrder;
        var savePointManager = entityFactory.getTransactionManager().getSavePointManager();
        try {
            savePointManager.createSavePoint(SAVE_POINT_NAME);
            savedOrder = orderRepository.saveOrder(order);
        } catch (Exception e) {
            savePointManager.rollbackToSavePoint(SAVE_POINT_NAME);
            throw new CriticalException("Can not create order", e);
        }
        return new OrderResp(savedOrder.getId(),
                savedOrder.getTimeOrder().toString(),
                status.getStatus(),
                orderDetails.stream()
                        .map(o -> new OrderResp.OrderDetailResp(String.valueOf(o.getAmount()), o.getProduct().getId(), o.getProduct().getName()))
                        .toList()
                        .toArray(new OrderResp.OrderDetailResp[0]));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public OrderResp updateOrder(OrderProductForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepository.getOrder(form.orderId());
        if (order == null) {
            throw new ResourceNotFoundException(String.format(ERROR_TEMPLATE, form.orderId()), "");
        }
        OrderStatusValue orderStatusValue;
        try {
            orderStatusValue = OrderStatusValue.valueOf(form.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadVariableException("Unknown order status");
        }
        order.getOrderStatus().setStatus(orderStatusValue.name());
        order = orderRepository.updateOrder(order);

        if (orderStatusValue.equals(OrderStatusValue.CONFIRMED)) {
            EntitySavePointManager savePointManager = entityFactory.getTransactionManager().getSavePointManager();
            savePointManager.createSavePoint(SAVE_POINT_NAME);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            order.getOrderDetails().forEach(detail -> atomicInteger.set(atomicInteger.get() + (detail.getAmount() * detail.getProduct().getOrderPoint())));
            customer.setOrderPoint(customer.getOrderPoint() + atomicInteger.get());
            try {
                customerRepository.updateCustomer(customer);
            } catch (Exception e) {
                log.error("Update customer order point failure", e);
                savePointManager.rollbackToSavePoint(SAVE_POINT_NAME);
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
    public OrderResp getOrderById(String orderId) {
        Order order = orderRepository.getOrder(orderId);
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
    public OrderInfoResp getOrderInfoById(String orderId) {
        Order order = orderRepository.getOrder(orderId);
        var info = new OrderInfoResp();
        info.setId(order.getId());
        info.setTimeOrder(order.getTimeOrder().toString());
        info.setOrderStatus(order.getOrderStatus().getStatus());
        info.setDeposited(String.valueOf(order.getBankTransactionCode().isEmpty()));
        return info;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean deleteOrder(String orderId) {
        Order order = orderRepository.getOrder(orderId);
        if (order == null) {
            throw new ResourceNotFoundException(String.format(ERROR_TEMPLATE, orderId), "");
        }
        return orderRepository.deleteOrder(order);
    }

    @Override
    public boolean orderIsExist(String orderId) {
        return orderRepository.isOrderExist(orderId);
    }

    @Override
    public Collection<OrderResp> getAllOrder() {
        return orderRepository.getAll()
                .stream()
                .map(order -> {
                    var id = order.getId();
                    var timeOrder = order.getTimeOrder().toString();
                    var status = order.getOrderStatus().getStatus();
                    var details = order.getOrderDetails()
                            .stream()
                            .map(orderDetail -> new OrderResp.OrderDetailResp(String.valueOf(orderDetail.getAmount()), orderDetail.getProduct().getId(), orderDetail.getProduct().getName()))
                            .toArray();
                    return new OrderResp(id, timeOrder, status, (OrderResp.OrderDetailResp[]) details);
                })
                .toList();
    }
}
