package com.bachlinh.order.trigger.spi;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ActiveReflection
public class IdGenTrigger extends AbstractTrigger<BaseEntity> {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(IdGenTrigger.class);
    private EntityFactory entityFactory;

    @ActiveReflection
    public IdGenTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
        setRunSync(true);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doExecute(BaseEntity entity) {
        EntityContext entityContext = entityFactory.getEntityContext(entity.getClass());
        if (entity.getId() == null) {
            entityContext.beginTransaction();
            Object id = entityContext.getNextId();
            if (log.isDebugEnabled()) {
                log.debug("Generate id for entity [{}] value [{}]", entity.getClass().getName(), id);
            }
            entity.setId(id);
            entityContext.commit();
        }
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    protected String getTriggerName() {
        return "idGenTrigger";
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT};
    }
}
