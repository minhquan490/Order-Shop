package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@ActiveReflection
public class DistrictIndexTrigger extends AbstractTrigger<District> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private EntityFactory entityFactory;

    @ActiveReflection
    public DistrictIndexTrigger(DependenciesResolver resolver) {
        super(resolver);
    }

    @Override
    protected void doExecute(District entity) {
        EntityContext entityContext = entityFactory.getEntityContext(District.class);
        if (log.isDebugEnabled()) {
            log.debug("Index district has name [{}]", (entity).getName());
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
        return "districtIndexTrigger";
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
