package com.bachlinh.order.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.concurrent.RunnableType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.trigger.AbstractTrigger;

import java.util.Collections;

@ActiveReflection
@ApplyOn(entity = Email.class)
public class IndexEmailContentTrigger extends AbstractTrigger<Email> {
    private EntityFactory entityFactory;

    @Override
    public String getTriggerName() {
        return "indexEmailContentTrigger";
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
    public void setResolver(DependenciesResolver resolver) {
        changeConcurrentType(RunnableType.INDEX);
        super.setResolver(resolver);
    }

    @Override
    protected void doExecute(Email entity) {
        entityFactory.getEntityContext(Email.class).analyze(Collections.singleton(entity));
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }
}
