package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.AbstractEntity_;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.VoucherRepository;
import jakarta.persistence.criteria.JoinType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@ActiveReflection
@BatchJob(name = "voucherCleaner")
public class VoucherCleaner extends AbstractJob {
    private static final int REMOVAL_POLICY = 3;
    private VoucherRepository voucherRepository;
    private CustomerRepository customerRepository;
    private LocalDateTime previousTimeExecution;

    private VoucherCleaner(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new VoucherCleaner(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (voucherRepository == null) {
            voucherRepository = resolveRepository(VoucherRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = resolveRepository(CustomerRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() {
        Where modifiedWhere = Where.builder().attribute(AbstractEntity_.MODIFIED_DATE).value(Timestamp.valueOf(LocalDateTime.now().plusMonths(-REMOVAL_POLICY))).operation(Operation.LT).build();
        Where enabledWhere = Where.builder().attribute(Voucher_.ACTIVE).value(false).operation(Operation.EQ).build();
        Join customerJoin = Join.builder().attribute(Voucher_.CUSTOMERS).type(JoinType.INNER).build();
        var vouchers = voucherRepository.getVouchers(Collections.emptyList(), Collections.singleton(customerJoin), Arrays.asList(modifiedWhere, enabledWhere));
        var customerUpdated = vouchers.stream()
                .filter(voucher -> !voucher.getCustomers().isEmpty())
                .map(voucher -> {
                    var customers = voucher.getCustomers();
                    customers.forEach(customer -> customer.getAssignedVouchers().remove(voucher));
                    voucher.getCustomers().clear();
                    return customers;
                })
                .reduce((customers, customers2) -> {
                    customers2.addAll(customers);
                    return customers2;
                })
                .orElse(HashSet.newHashSet(0));
        customerRepository.updateCustomers(customerUpdated);
        voucherRepository.updateVouchers(vouchers);
        voucherRepository.deleteVouchers(vouchers);
        previousTimeExecution = LocalDateTime.now();
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return getPreviousExecutionTime().plusMonths(REMOVAL_POLICY);
    }

    @Override
    protected LocalDateTime doGetPreviousExecutionTime() {
        if (previousTimeExecution == null) {
            previousTimeExecution = LocalDateTime.now();
        }
        return previousTimeExecution;
    }

    @Override
    public JobType getJobType() {
        return JobType.MONTHLY;
    }
}
