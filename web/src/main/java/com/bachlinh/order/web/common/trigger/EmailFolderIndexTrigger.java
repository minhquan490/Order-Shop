package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.EmailFolders;

@ActiveReflection
@ApplyOn(entity = EmailFolders.class)
public class EmailFolderIndexTrigger extends AbstractRepositoryTrigger<EmailFolders> {

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
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
        return "emailFoldersIndexTrigger";
    }
}
