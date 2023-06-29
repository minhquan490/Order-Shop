package com.bachlinh.order.trigger.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
public class EmailTemplateIndexTrigger extends AbstractTrigger<EmailTemplate> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private EntityFactory entityFactory;

    @ActiveReflection
    public EmailTemplateIndexTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
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
    public void doExecute(EmailTemplate entity) {
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
    protected String getTriggerName() {
        return "emailTemplateIndexTrigger";
    }
}
