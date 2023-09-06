package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@ActiveReflection
@ApplyOn(entity = Province.class)
public class ProvinceIndexTrigger extends AbstractTrigger<Province> {
    private static final int TOTAL_PROVINCE = 63;
    private final Collection<Province> caches = Collections.synchronizedList(new ArrayList<>(TOTAL_PROVINCE));

    private EntityFactory entityFactory;

    @Override
    protected void doExecute(Province entity) {
        EntityContext entityContext = entityFactory.getEntityContext(Province.class);
        if (caches.size() != TOTAL_PROVINCE - 1) {
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
        return "provinceIndexTrigger";
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_UPDATE, TriggerExecution.ON_INSERT};
    }

    @Override
    public void setResolver(DependenciesResolver resolver) {
        changeConcurrentType(RunnableType.INDEX);
        super.setResolver(resolver);
    }
}
