package com.bachlinh.order.web.common.trigger;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.ApplyOn;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.web.repository.spi.UserAssignmentRepository;

@ApplyOn(entity = Voucher.class)
@ActiveReflection
public class VoucherDeletionTrigger extends AbstractRepositoryTrigger<Voucher> {

    private UserAssignmentRepository userAssignmentRepository;

    @Override
    public TriggerMode getMode() {
        return TriggerMode.BEFORE;
    }

    @Override
    public TriggerExecution[] getExecuteOn() {
        return new TriggerExecution[]{TriggerExecution.ON_DELETE};
    }

    @Override
    public String getTriggerName() {
        return "VoucherDeletion";
    }

    @Override
    protected void doExecute(Voucher entity) {
        userAssignmentRepository.deleteUserAssignment(entity);
    }

    @Override
    protected void inject() {
        if (userAssignmentRepository == null) {
            userAssignmentRepository = resolveRepository(UserAssignmentRepository.class);
        }
    }
}
