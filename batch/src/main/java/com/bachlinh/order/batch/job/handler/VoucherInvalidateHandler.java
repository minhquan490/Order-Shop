package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.VoucherRepository;
import jakarta.persistence.criteria.JoinType;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@ActiveReflection
@BatchJob(name = "voucherInvalidate")
public class VoucherInvalidateHandler extends AbstractJob {
    private VoucherRepository voucherRepository;
    private CustomerRepository customerRepository;
    private LocalDateTime previousTimeExecution;

    private VoucherInvalidateHandler(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new VoucherInvalidateHandler(name, activeProfile, dependenciesResolver);
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
        Where timeExpiryWhere = Where.builder().attribute(Voucher_.TIME_EXPIRED).value(Timestamp.from(Instant.now())).operation(Operation.LT).build();
        Where enabledWhere = Where.builder().attribute(Voucher_.ACTIVE).value(true).operation(Operation.EQ).build();
        Join assigneredJoin = Join.builder().attribute(Voucher_.CUSTOMERS).type(JoinType.INNER).build();
        var vouchers = voucherRepository.getVouchers(Collections.emptyList(), Collections.singletonList(assigneredJoin), Arrays.asList(timeExpiryWhere, enabledWhere));
        var updatedCustomers = vouchers.stream()
                .map(voucher -> {
                    var customers = voucher.getCustomers();
                    customers.forEach(customer -> customer.getAssignedVouchers().remove(voucher));
                    voucher.getCustomers().clear();
                    voucher.setActive(false);
                    return customers;
                })
                .reduce((customers, customers2) -> {
                    customers2.addAll(customers);
                    return customers2;
                })
                .orElse(HashSet.newHashSet(0));
        customerRepository.updateCustomers(updatedCustomers);
        voucherRepository.updateVouchers(vouchers);
        previousTimeExecution = LocalDateTime.now();
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return getPreviousExecutionTime().plusDays(1);
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
        return JobType.DAILY;
    }
}
