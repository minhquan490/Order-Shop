package com.bachlinh.order.core.entity.trigger.spi;

import com.bachlinh.order.annotation.Ignore;
import com.bachlinh.order.core.entity.model.AbstractEntity;
import com.bachlinh.order.core.entity.model.BaseEntity;
import com.bachlinh.order.core.entity.model.Customer;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.AuditorAware;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Ignore
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class AuditingTrigger extends AbstractTrigger<AbstractEntity> {
    private AuditorAware<Object> entityAuditor;
    private final TriggerMode mode;

    public AuditingTrigger(ApplicationContext applicationContext, TriggerMode mode) {
        super(applicationContext);
        this.mode = mode;
        setRunSync(true);
    }

    @Override
    public void doExecute(BaseEntity entity) {
        if (log.isDebugEnabled()) {
            log.debug("BEGIN Auditing trigger");
            log.debug("Execute on entity [{}]", entity.getClass().getName());
        }
        Optional<Object> auditor = getEntityAuditor().getCurrentAuditor();
        if (auditor.isEmpty()) {
            auditor = Optional.of("Application");
        }
        Object audit = auditor.get();
        AbstractEntity abstractEntity = (AbstractEntity) entity;
        if (abstractEntity.getCreatedBy() == null) {
            if (audit instanceof Customer holder) {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                abstractEntity.setCreatedBy(holder.getId());
                abstractEntity.setCreatedDate(now);
                abstractEntity.setModifiedBy(holder.getId());
                abstractEntity.setModifiedDate(now);
            } else {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                assert audit instanceof String;
                abstractEntity.setCreatedBy((String) audit);
                abstractEntity.setCreatedDate(now);
                abstractEntity.setModifiedBy((String) audit);
                abstractEntity.setModifiedDate(now);
            }
        } else {
            if (audit instanceof Customer holder) {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                abstractEntity.setModifiedBy(holder.getId());
                abstractEntity.setModifiedDate(now);
            } else {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                abstractEntity.setModifiedBy((String) audit);
                abstractEntity.setModifiedDate(now);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("END Auditing trigger");
        }
    }

    @Override
    protected String getTriggerName() {
        return "auditingTrigger";
    }

    @SuppressWarnings("unchecked")
    private AuditorAware<Object> getEntityAuditor() {
        if (entityAuditor == null) {
            this.entityAuditor = getApplicationContext().getBean(AuditorAware.class);
        }
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
