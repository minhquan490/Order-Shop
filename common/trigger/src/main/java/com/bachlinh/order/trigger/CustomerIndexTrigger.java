package com.bachlinh.order.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.trigger.AbstractTrigger;

@ActiveReflection
@ApplyOn(entity = Customer.class)
public class CustomerIndexTrigger extends AbstractTrigger<Customer> {
    private EntityFactory entityFactory;

    @Override
    protected void doExecute(Customer entity) {
        EntityContext context = entityFactory.getEntityContext(Customer.class);
        context.analyze(entity);
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            this.entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
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

    @Override
    public void setResolver(DependenciesResolver resolver) {
        changeConcurrentType(RunnableType.INDEX);
        super.setResolver(resolver);
    }
}
