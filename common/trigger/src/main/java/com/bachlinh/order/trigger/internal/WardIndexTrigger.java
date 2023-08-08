package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@ActiveReflection
@ApplyOn(entity = Ward.class)
public class WardIndexTrigger extends AbstractTrigger<Ward> {
    private static final int TOTAL_WARD = 10598;
    private final Collection<Ward> caches = Collections.synchronizedList(new ArrayList<>(TOTAL_WARD));

    private EntityFactory entityFactory;

    @ActiveReflection
    public WardIndexTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
        changeConcurrentType(RunnableType.INDEX);
    }

    @Override
    protected void doExecute(Ward entity) {
        EntityContext entityContext = entityFactory.getEntityContext(Ward.class);
        if (caches.size() != TOTAL_WARD - 1) {
            caches.add(entity);
        } else {
            caches.add(entity);
            try {
                entityContext.analyze(new ArrayList<>(caches));
            } catch (Exception e) {
                log.error("Can not index province has name [{}]", entity.getName(), e);
            }
            caches.clear();
        }
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
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
