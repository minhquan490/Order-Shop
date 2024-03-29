package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.DirectMessage;

@ActiveReflection
@ApplyOn(entity = DirectMessage.class)
public class DirectMessageIndexTrigger extends AbstractRepositoryTrigger<DirectMessage> {

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
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
        return "directMessageIndexTrigger";
    }
}
