package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
@ApplyOn(entity = Customer.class)
public class CreateCustomerDefaultDataTrigger extends AbstractTrigger<Customer> {
    private static final String EMPTY_STRING = "";

    private EntityFactory entityFactory;

    @ActiveReflection
    public CreateCustomerDefaultDataTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
        setRunSync(true);
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }

    @Override
    protected void doExecute(Customer entity) {
        entity.setFirstName(EMPTY_STRING);
        entity.setLastName(EMPTY_STRING);
        entity.setPhoneNumber(EMPTY_STRING);
        entity.setGender(EMPTY_STRING);
        entity.setOrderPoint(0);
        entity.setActivated(false);
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
        return "createCustomerCommonDataTrigger";
    }
}
