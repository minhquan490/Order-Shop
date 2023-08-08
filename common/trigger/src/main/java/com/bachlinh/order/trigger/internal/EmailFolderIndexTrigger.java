package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

@ActiveReflection
@ApplyOn(entity = EmailFolders.class)
public class EmailFolderIndexTrigger extends AbstractTrigger<EmailFolders> {

    private EntityFactory entityFactory;

    @ActiveReflection
    public EmailFolderIndexTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
        changeConcurrentType(RunnableType.INDEX);
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT, TriggerExecution.ON_UPDATE};
    }

    @Override
    protected void doExecute(EmailFolders entity) {
        if (log.isDebugEnabled()) {
            log.debug("BEGIN: Index EmailFolders with name [{}]", entity.getName());
        }
        entityFactory.getEntityContext(EmailFolders.class).analyze(entity);
        if (log.isDebugEnabled()) {
            log.debug("END: Index EmailFolders with name [{}] done", entity.getName());
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
        return "emailFoldersIndexTrigger";
    }
}
