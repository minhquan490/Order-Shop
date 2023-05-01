package com.bachlinh.order.trigger.spi;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Ignore;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.service.container.DependenciesResolver;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Ignore
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ActiveReflection
public class AuditingTrigger extends AbstractTrigger<AbstractEntity> {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AuditingTrigger.class);
    private AuditorAware<Object> entityAuditor;
    private final TriggerMode mode;

    @ActiveReflection
    public AuditingTrigger(DependenciesResolver dependenciesResolver) {
        super(dependenciesResolver);
        this.mode = TriggerMode.BEFORE;
        setRunSync(true);
    }

    @Override
    public void doExecute(AbstractEntity entity) {
        if (log.isDebugEnabled()) {
            log.debug("BEGIN Auditing trigger");
            log.debug("Execute on entity [{}]", entity.getClass().getName());
        }
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
        if (log.isDebugEnabled()) {
            log.debug("END Auditing trigger");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void inject() {
        if (entityAuditor == null) {
            this.entityAuditor = getDependenciesResolver().resolveDependencies(AuditorAware.class);
        }
    }

    @Override
    protected String getTriggerName() {
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
        return new TriggerExecution[]{TriggerExecution.ON_INSERT, TriggerExecution.ON_DELETE};
    }
}
