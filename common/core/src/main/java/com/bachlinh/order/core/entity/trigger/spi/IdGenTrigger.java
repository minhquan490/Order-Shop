package com.bachlinh.order.core.entity.trigger.spi;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.entity.EntityFactory;
import com.bachlinh.order.core.entity.context.spi.EntityContext;
import com.bachlinh.order.core.entity.model.BaseEntity;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
@ActiveReflection
public class IdGenTrigger extends AbstractTrigger<BaseEntity> {
    private EntityFactory entityFactory;

    @ActiveReflection
    public IdGenTrigger(ApplicationContext applicationContext) {
        super(applicationContext);
        setRunSync(true);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doExecute(BaseEntity entity) {
        if (entityFactory == null) {
            entityFactory = getApplicationContext().getBean(EntityFactory.class);
        }
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
