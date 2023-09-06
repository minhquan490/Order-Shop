package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
@ApplyOn(entity = EmailTemplate.class)
public class EmailTemplateIndexTrigger extends AbstractTrigger<EmailTemplate> {

    private EntityFactory entityFactory;

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

    @Override
    protected void doExecute(EmailTemplate entity) {
        if (log.isDebugEnabled()) {
            log.debug("BEGIN: Index EmailTemplate with name [{}]", entity.getName());
        }
        entityFactory.getEntityContext(EmailTemplate.class).analyze(entity);
        if (log.isDebugEnabled()) {
            log.debug("END: Index EmailTemplate with name [{}] done", entity.getName());
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
        return "emailTemplateIndexTrigger";
    }
}
