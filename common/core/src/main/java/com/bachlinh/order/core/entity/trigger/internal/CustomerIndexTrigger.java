package com.bachlinh.order.core.entity.trigger.internal;

import com.bachlinh.order.core.entity.EntityFactory;
import com.bachlinh.order.core.entity.context.spi.EntityContext;
import com.bachlinh.order.core.entity.model.BaseEntity;
import com.bachlinh.order.core.entity.model.Customer;
import com.bachlinh.order.core.entity.trigger.spi.AbstractTrigger;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import org.springframework.context.ApplicationContext;

import java.util.Collections;

public class CustomerIndexTrigger extends AbstractTrigger<Customer> {
    private EntityFactory entityFactory;

    public CustomerIndexTrigger(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public void doExecute(BaseEntity entity) {
        if (entity == null) {
            this.entityFactory = getApplicationContext().getBean(EntityFactory.class);
        }
        Customer customer = (Customer) entity;
        EntityContext context = entityFactory.getEntityContext(Customer.class);
        context.analyze(Collections.singleton(customer));
    }

    @Override
    protected String getTriggerName() {
        return "customerIndexTrigger";
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT, TriggerExecution.ON_UPDATE};
    }
}
