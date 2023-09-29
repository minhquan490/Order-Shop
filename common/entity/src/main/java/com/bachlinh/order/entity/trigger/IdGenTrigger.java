package com.bachlinh.order.entity.trigger;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.BaseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ActiveReflection
@ApplyOn(order = Ordered.HIGHEST_PRECEDENCE, type = ApplyOn.ApplyType.ALL, entity = BaseEntity.class)
public class IdGenTrigger extends AbstractTrigger<BaseEntity<?>> {

    private EntityFactory entityFactory;

    @Override
    protected void doExecute(BaseEntity<?> entity) {
        EntityContext entityContext = entityFactory.getEntityContext(entity.getClass());
        if (entity.getId() == null) {
            Object id = entityContext.getNextId();
            entity.setId(id);
            entity.setNew(true);
        }
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
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

    @Override
    public void setResolver(DependenciesResolver resolver) {
        setRunSync(true);
        super.setResolver(resolver);
    }
}
