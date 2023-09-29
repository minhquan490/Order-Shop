package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.OrderHistoryRepository;
import com.bachlinh.order.web.dto.resp.OrderHistoryResp;
import com.bachlinh.order.web.service.common.OrderHistoryService;

import java.util.Collection;

@ServiceComponent
@ActiveReflection
public class OrderHistoryServiceImpl extends AbstractService implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;
    private final DtoMapper dtoMapper;

    private OrderHistoryServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.orderHistoryRepository = resolveRepository(OrderHistoryRepository.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
    }

    @Override
    public Collection<OrderHistoryResp> getOrderHistoriesOfCustomer(Customer owner) {
        var histories = orderHistoryRepository.getHistoriesOfCustomer(owner);
        return dtoMapper.map(histories, OrderHistoryResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new OrderHistoryServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{OrderHistoryService.class};
    }
}
