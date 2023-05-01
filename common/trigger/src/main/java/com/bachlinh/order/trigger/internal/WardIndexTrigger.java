package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;

@Log4j2
@ActiveReflection
public class WardIndexTrigger extends AbstractTrigger<Ward> {
    private EntityFactory entityFactory;

    @ActiveReflection
    public WardIndexTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    public void doExecute(Ward entity) {
        EntityContext entityContext = entityFactory.getEntityContext(Ward.class);
        if (log.isDebugEnabled()) {
            log.debug("Index ward has name [{}]", (entity).getName());
        }
        entityContext.analyze(Collections.singleton(entity));
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    protected String getTriggerName() {
        return "wardIndexTrigger";
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
