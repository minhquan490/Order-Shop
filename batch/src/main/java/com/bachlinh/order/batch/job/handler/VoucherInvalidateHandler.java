package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.repository.CustomerRepository;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesResolver;
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

    @ActiveReflection
    public VoucherInvalidateHandler(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (voucherRepository == null) {
            voucherRepository = getDependenciesResolver().resolveDependencies(VoucherRepository.class);
        }
        if (customerRepository == null) {
            customerRepository = getDependenciesResolver().resolveDependencies(CustomerRepository.class);
        }
    }

    @Override
    protected void doExecuteInternal() throws Exception {
        Where timeExpiryWhere = Where.builder().attribute(Voucher_.TIME_EXPIRED).value(Timestamp.from(Instant.now())).operator(Operator.LT).build();
        Where enabledWhere = Where.builder().attribute(Voucher_.ACTIVE).value(true).operator(Operator.EQ).build();
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
                .orElse(new HashSet<>(0));
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
