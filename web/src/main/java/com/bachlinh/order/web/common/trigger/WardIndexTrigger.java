package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.Ward;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@ActiveReflection
@ApplyOn(entity = Ward.class)
public class WardIndexTrigger extends AbstractRepositoryTrigger<Ward> {
    private static final int TOTAL_WARD = 10598;
    private Collection<Ward> caches;

    private EntityFactory entityFactory;

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
        if (caches == null) {
            caches = Collections.synchronizedList(new ArrayList<>(TOTAL_WARD));
        }
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
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

    @Override
    public void setResolver(DependenciesResolver resolver) {
        changeConcurrentType(RunnableType.INDEX);
        super.setResolver(resolver);
    }
}
