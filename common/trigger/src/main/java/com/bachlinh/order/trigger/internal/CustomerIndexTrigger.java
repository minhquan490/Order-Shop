package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
public class CustomerIndexTrigger extends AbstractTrigger<Customer> {
    private EntityFactory entityFactory;

    @ActiveReflection
    public CustomerIndexTrigger(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void doExecute(Customer entity) {
        EntityContext context = entityFactory.getEntityContext(Customer.class);
        context.analyze(entity);
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            this.entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
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
