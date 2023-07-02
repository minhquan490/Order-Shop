package com.bachlinh.order.trigger.internal;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.repository.CustomerInfoChangerHistoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

@ActiveReflection
public class CustomerInfoChangeHistoryTrigger extends AbstractTrigger<CustomerInfoChangeHistory> {
    private CustomerInfoChangerHistoryRepository repository;
    private EntityFactory entityFactory;

    @ActiveReflection
    public CustomerInfoChangeHistoryTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
    }

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_UPDATE};
    }

    @Override
    protected void doExecute(CustomerInfoChangeHistory entity) {
        var histories = new ArrayList<CustomerInfoChangeHistory>();
        var updatedFields = entity.getUpdatedFields();
        for (var field : updatedFields) {
            var history = entityFactory.getEntity(CustomerInfoChangeHistory.class);
            history.setCustomer(entity.getCustomer());
            history.setFieldName(field.fieldName());
            history.setOldValue(field.value());
            history.setTimeUpdate(Timestamp.from(Instant.now()));
            histories.add(history);
        }
        if (!histories.isEmpty()) {
            repository.saveHistories(histories);
        }
    }

    @Override
    protected void inject() {
        if (repository == null) {
            repository = getDependenciesResolver().resolveDependencies(CustomerInfoChangerHistoryRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    protected String getTriggerName() {
        return "customerInfoChangeHistoryTrigger";
    }
}
