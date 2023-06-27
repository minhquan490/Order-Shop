package com.bachlinh.order.trigger.internal;

import lombok.extern.slf4j.Slf4j;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
@Slf4j
public class EmailTemplateFolderIndexTrigger extends AbstractTrigger<EmailTemplateFolder> {
    private EntityFactory entityFactory;

    @ActiveReflection
    public EmailTemplateFolderIndexTrigger(DependenciesResolver dependenciesResolver) {
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
    public void doExecute(EmailTemplateFolder entity) {
        if (log.isDebugEnabled()) {
            log.debug("BEGIN: Index EmailTemplateFolder with name [{}]", entity.getName());
        }
        entityFactory.getEntityContext(EmailTemplateFolder.class).analyze(entity);
        if (log.isDebugEnabled()) {
            log.debug("END: Index EmailTemplateFolder with name [{}] done", entity.getName());
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
        return "emailTemplateFolderIndexTrigger";
    }
}
