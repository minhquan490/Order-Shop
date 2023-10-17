package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.Product;

import java.util.Collections;

@ActiveReflection
@ApplyOn(entity = Product.class)
public class ProductIndexTrigger extends AbstractRepositoryTrigger<Product> {
    private EntityFactory entityFactory;

    @Override
    protected void doExecute(Product entity) {
        EntityContext entityContext = entityFactory.getEntityContext(entity.getClass());
        entityContext.analyze(Collections.singleton(entity));
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            this.entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
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

    @Override
    public void setResolver(DependenciesResolver resolver) {
        changeConcurrentType(RunnableType.INDEX);
        super.setResolver(resolver);
    }
}
