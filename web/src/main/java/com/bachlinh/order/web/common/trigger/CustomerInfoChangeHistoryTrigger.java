package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.web.repository.spi.CustomerInfoChangeHistoryRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

@ActiveReflection
@ApplyOn(entity = Customer.class)
public class CustomerInfoChangeHistoryTrigger extends AbstractRepositoryTrigger<Customer> {
    private CustomerInfoChangeHistoryRepository repository;
    private EntityFactory entityFactory;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.AFTER;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_UPDATE};
    }

    @Override
    protected void doExecute(Customer entity) {
        var histories = new ArrayList<CustomerInfoChangeHistory>();
        var updatedFields = entity.getUpdatedFields();
        for (var field : updatedFields) {
            var history = entityFactory.getEntity(CustomerInfoChangeHistory.class);
            history.setCustomer(entity);
            history.setFieldName(field.fieldName());
            history.setOldValue(String.valueOf(field.oldValue()));
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
            repository = resolveRepository(CustomerInfoChangeHistoryRepository.class);
        }
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
    }

    @Override
    public String getTriggerName() {
        return "customerInfoChangeHistoryTrigger";
    }
}
