package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

import java.util.Collections;

@ActiveReflection
public class ProductIndexTrigger extends AbstractTrigger<Product> {
    private EntityFactory entityFactory;

    @ActiveReflection
    public ProductIndexTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    public void doExecute(Product entity) {
        EntityContext entityContext = entityFactory.getEntityContext(entity.getClass());
        entityContext.analyze(Collections.singleton(entity));
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            this.entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    protected String getTriggerName() {
        return "indexProductTrigger";
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT, TriggerExecution.ON_UPDATE};
    }
}
