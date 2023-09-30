package com.bachlinh.order.entity.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ActiveReflection
@ApplyOn(order = Ordered.HIGHEST_PRECEDENCE + 1, type = ApplyOn.ApplyType.ALL, entity = BaseEntity.class)
public class AuditingTrigger extends AbstractTrigger<AbstractEntity<?>> {

    private AuditorAware<Object> entityAuditor;
    private TriggerMode mode;

    @Override
    protected void doExecute(AbstractEntity<?> entity) {
        Optional<Object> auditor = getEntityAuditor().getCurrentAuditor();
        if (auditor.isEmpty()) {
            auditor = Optional.of("Application");
        }
        Object audit = auditor.get();
        if (entity.getCreatedBy() == null) {
            if (audit instanceof Customer holder) {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                entity.setCreatedBy(holder.getId());
                entity.setCreatedDate(now);
                entity.setModifiedBy(holder.getId());
                entity.setModifiedDate(now);
            } else {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                assert audit instanceof String;
                entity.setCreatedBy((String) audit);
                entity.setCreatedDate(now);
                entity.setModifiedBy((String) audit);
                entity.setModifiedDate(now);
            }
        } else {
            if (audit instanceof Customer holder) {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                entity.setModifiedBy(holder.getId());
                entity.setModifiedDate(now);
            } else {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                entity.setModifiedBy((String) audit);
                entity.setModifiedDate(now);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void inject() {
        if (entityAuditor == null) {
            this.entityAuditor = resolveDependencies(AuditorAware.class);
        }
    }

    @Override
    public String getTriggerName() {
        return "auditingTrigger";
    }

    private AuditorAware<Object> getEntityAuditor() {
        return this.entityAuditor;
    }

    @Override
    public TriggerMode getMode() {
        return mode;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_INSERT, TriggerExecution.ON_UPDATE};
    }

    @Override
    public void setResolver(DependenciesResolver resolver) {
        this.mode = TriggerMode.BEFORE;
        setRunSync(true);
        super.setResolver(resolver);
    }
}
