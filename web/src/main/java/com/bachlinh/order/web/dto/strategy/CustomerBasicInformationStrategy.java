package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.dto.strategy.DtoStrategy;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerBasicInformationResp;

import java.text.MessageFormat;

@ActiveReflection
public class CustomerBasicInformationStrategy extends AbstractDtoStrategy<CustomerBasicInformationResp, Customer> {

    private String servingFileUrl;

    @ActiveReflection
    private CustomerBasicInformationStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(Customer source, Class<CustomerBasicInformationResp> type) {
        // Do nothing
    }

    @Override
    protected CustomerBasicInformationResp doConvert(Customer source, Class<CustomerBasicInformationResp> type) {
        CustomerBasicInformationResp resp = new CustomerBasicInformationResp();
        resp.setCustomerId(source.getId());
        resp.setRole(source.getRole());
        resp.setLastName(source.getLastName());
        resp.setFirstName(source.getFirstName());
        if (source.getCustomerMedia() == null) {
            resp.setAvatarUrl("");
        } else {
            if (servingFileUrl == null) {
                Environment environment = getEnvironment();
                servingFileUrl = MessageFormat.format("https://{0}:{1}{2}?id={3}", environment.getProperty("server.address"), environment.getProperty("server.port"), environment.getProperty("shop.url.resource"), source.getCustomerMedia().getId());
            }
            resp.setAvatarUrl(servingFileUrl);
        }
        return resp;
    }

    @Override
    protected void afterConvert(Customer source, Class<CustomerBasicInformationResp> type) {
        // Do nothing
    }

    @Override
    protected DtoStrategy<CustomerBasicInformationResp, Customer> createNew(DependenciesResolver dependenciesResolver, Environment environment) {
        return new CustomerBasicInformationStrategy(dependenciesResolver, environment);
    }

    @Override
    public Class<CustomerBasicInformationResp> getTargetType() {
        return CustomerBasicInformationResp.class;
    }
}
