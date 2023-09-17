package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.OrderStatusValue;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.transaction.spi.EntitySavePointManager;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.OrderRepository;
import com.bachlinh.order.repository.ProductRepository;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.form.admin.order.OrderChangeStatusForm;
import com.bachlinh.order.web.dto.form.admin.order.OrderProductForm;
import com.bachlinh.order.web.dto.form.customer.OrderCreateForm;
import com.bachlinh.order.web.dto.resp.AnalyzeOrderNewInMonthResp;
import com.bachlinh.order.web.dto.resp.OrderInfoResp;
import com.bachlinh.order.web.dto.resp.OrderListResp;
import com.bachlinh.order.web.dto.resp.OrderOfCustomerResp;
import com.bachlinh.order.web.dto.resp.OrderResp;
import com.bachlinh.order.web.service.business.OrderAnalyzeService;
import com.bachlinh.order.web.service.business.OrderChangeStatusService;
import com.bachlinh.order.web.service.business.OrderInDateService;
import com.bachlinh.order.web.service.common.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
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
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService, OrderChangeStatusService, OrderInDateService, OrderAnalyzeService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String SAVE_POINT_NAME = "customerOrderPoint";
    private static final String ERROR_TEMPLATE = "Order with id [%s] not found";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;
    private final MessageSettingRepository messageSettingRepository;

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
    public Collection<OrderListResp> getOrdersInDate() {
        List<Order> orders = orderRepository.getNewOrdersInDate();
        return orders.stream()
                .map(order -> dtoMapper.map(order, OrderListResp.class))
                .toList();
    }

    @Override
    public int numberOrderInDate() {
        return getOrdersInDate().size();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public OrderResp saveOrder(OrderCreateForm form) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Product> productMap = productRepository.getProductsForSavingOrder(Stream.of(form.getDetails()).map(OrderCreateForm.Detail::getProductId).toList())
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
        return dtoMapper.map(order, OrderInfoResp.class);
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

    @Override
    public OrderOfCustomerResp getOrdersOfCustomer(NativeRequest<?> request) {
        long page = getPage(request);
        long pageSize = getPageSize(request);
        String customerId = getCustomerId(request, request.getUrl());

        Collection<Order> orders = orderRepository.getOrdersOfCustomer(customerId, page, pageSize);
        Collection<OrderOfCustomerResp.OrderInfo> orderInfos = dtoMapper.map(orders, OrderOfCustomerResp.OrderInfo.class);

        OrderOfCustomerResp resp = new OrderOfCustomerResp();
        resp.setOrderInfos(orderInfos);
        Long totalOrders = orderRepository.countOrdersOfCustomer(customerId);
        resp.setTotalOrder(totalOrders);
        resp.setPage(page);
        resp.setPageSize(pageSize);
        return resp;
    }

    @Override
    public AnalyzeOrderNewInMonthResp analyzeNewOrderInMonth() {
        var template = "select t.* from (select count(o.id) as first, ({0}) as second, ({1}) as third, ({2}) as fourth, ({3}) as last from Order o where o.created_date between :firstStart and :firstEnd) as t";
        var secondStatement = "select count(o.id) from Order o where o.created_date between :secondStart and :secondEnd";
        var thirdStatement = "select count(o.id) from Order o where o.created_date between :thirdStart and :thirdEnd";
        var fourthStatement = "select count(o.id) from Order o where o.created_date between :fourthStart and :fourthEnd";
        var lastStatement = "select count(o.id) from Order o where o.created_date between :lastStart and :lastEnd";
        var query = MessageFormat.format(template, secondStatement, thirdStatement, fourthStatement, lastStatement);
        var attributes = new HashMap<String, Object>(10);
        var now = LocalDateTime.now();
        var firstParam = Timestamp.valueOf(now.plusWeeks(-5));
        var secondParam = Timestamp.valueOf(now.plusWeeks(-4));
        var thirdParam = Timestamp.valueOf(now.plusWeeks(-3));
        var fourthParam = Timestamp.valueOf(now.plusWeeks(-2));
        var fifthParam = Timestamp.valueOf(now.plusWeeks(-1));
        attributes.put("firstStart", firstParam);
        attributes.put("firstEnd", secondParam);
        attributes.put("secondStart", secondParam);
        attributes.put("secondEnd", thirdParam);
        attributes.put("thirdStart", thirdParam);
        attributes.put("thirdEnd", fourthParam);
        attributes.put("fourthStart", fourthParam);
        attributes.put("fourthEnd", fifthParam);
        attributes.put("lastStart", fifthParam);
        attributes.put("lastEnd", Timestamp.valueOf(now));
        var result = orderRepository.getResultList(query, attributes, AnalyzeOrderNewInMonthResp.ResultSet.class).get(0);
        return dtoMapper.map(result, AnalyzeOrderNewInMonthResp.class);
    }

    private String getCustomerId(NativeRequest<?> nativeRequest, String path) {
        String customerId = nativeRequest.getUrlQueryParam().getFirst("customerId");
        if (!StringUtils.hasText(customerId)) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000008");
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Customer");
            throw new ResourceNotFoundException(errorContent, path);
        }
        return customerId;
    }

    private long getPageSize(NativeRequest<?> nativeRequest) {
        String pageSizeRequestParam = nativeRequest.getUrlQueryParam().getFirst("pageSize");
        if (ValidateUtils.isNumber(pageSizeRequestParam)) {
            return Long.parseLong(pageSizeRequestParam);
        } else {
            return 50L;
        }
    }

    private long getPage(NativeRequest<?> nativeRequest) {
        String pageRequestParam = nativeRequest.getUrlQueryParam().getFirst("page");
        if (ValidateUtils.isNumber(pageRequestParam)) {
            return Long.parseLong(pageRequestParam);
        } else {
            return 1L;
        }
    }
}
