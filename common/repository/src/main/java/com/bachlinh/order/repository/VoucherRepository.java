package com.bachlinh.order.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.Where;

import java.util.Collection;

public interface VoucherRepository {
    Voucher saveVoucher(Voucher voucher);

    Voucher updateVoucher(Voucher voucher);

    Voucher getVoucher(@NonNull Collection<Select> selects, @NonNull Collection<Join> joins, @NonNull Collection<Where> wheres);

    Voucher getVoucherById(@NonNull Collection<Select> selects, String id);

    boolean isVoucherNameExist(String voucherName);

    boolean isVoucherIdExist(String voucherId);

    void deleteVoucher(Voucher voucher);

    Collection<Voucher> getListVoucher(Pageable pageable, Sort sort);

    Collection<Voucher> getVouchers(Collection<Select> selects, Collection<Join> joins, Collection<Where> wheres);

    Collection<Voucher> getVouchersByIds(Collection<String> ids);
}
