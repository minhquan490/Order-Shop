package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;

@ActiveReflection
@Log4j2
public class ProvinceIndexTrigger extends AbstractTrigger<Province> {
    private EntityFactory entityFactory;

    @ActiveReflection
    public ProvinceIndexTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    public void doExecute(Province entity) {
        EntityContext entityContext = entityFactory.getEntityContext(Province.class);
        if (log.isDebugEnabled()) {
            log.debug("Index province has name [{}]", entity.getName());
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
}
