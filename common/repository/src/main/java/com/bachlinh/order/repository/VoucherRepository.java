package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface VoucherRepository {
    Voucher saveVoucher(Voucher voucher);

    Voucher updateVoucher(Voucher voucher);

    void deleteVoucher(Voucher voucher);

    Page<Voucher> getListVoucher(Pageable pageable, Sort sort);
}
