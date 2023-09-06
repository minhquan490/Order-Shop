package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.DirectMessage;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
@ApplyOn(entity = DirectMessage.class)
public class DirectMessageIndexTrigger extends AbstractTrigger<DirectMessage> {

    private EntityFactory entityFactory;

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

    @Override
    protected void doExecute(DirectMessage entity) {
        if (log.isDebugEnabled()) {
            log.debug("BEGIN: Index DirectMessage with content [{}]", entity.getContent());
        }
        entityFactory.getEntityContext(DirectMessage.class).analyze(entity);
        if (log.isDebugEnabled()) {
            log.debug("END: Index DirectMessage with content [{}] done", entity.getContent());
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
        return "directMessageIndexTrigger";
    }
}
