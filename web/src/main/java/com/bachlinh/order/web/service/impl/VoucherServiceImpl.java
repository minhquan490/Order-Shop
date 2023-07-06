package com.bachlinh.order.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherCreateForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherSearchForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;
import com.bachlinh.order.web.dto.resp.VoucherResp;
import com.bachlinh.order.web.service.business.VoucherSearchService;
import com.bachlinh.order.web.service.common.VoucherService;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ServiceComponent
@ActiveReflection
@RequiredArgsConstructor(onConstructor = @__(@ActiveReflection))
public class VoucherServiceImpl implements VoucherService, VoucherSearchService {
    private final VoucherRepository voucherRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public VoucherResp createVoucher(VoucherCreateForm form) {
        var voucher = entityFactory.getEntity(Voucher.class);
        voucher.setName(form.getName());
        voucher.setDiscountPercent(form.getDiscounterPercent());
        voucher.setTimeStart(Timestamp.valueOf(form.getTimeStart()));
        voucher.setTimeExpired(Timestamp.valueOf(form.getTimeEnd()));
        voucher.setVoucherContent(form.getContent());
        voucher.setVoucherCost(form.getCost());
        voucher.setActive(form.isEnable());
        voucher = voucherRepository.saveVoucher(voucher);
        return dtoMapper.map(voucher, VoucherResp.class);
    }

    @Override
    public VoucherResp updateVoucher(VoucherUpdateForm form) {
        var voucher = voucherRepository.getVoucherById(Collections.emptyList(), form.getId());
        voucher.setName(form.getName());
        voucher.setDiscountPercent(form.getDiscountPercent());
        voucher.setTimeStart(Timestamp.valueOf(form.getTimeStart()));
        voucher.setTimeExpired(Timestamp.valueOf(form.getTimeEnd()));
        voucher.setVoucherContent(form.getContent());
        voucher.setVoucherCost(form.getCost());
        voucher.setActive(form.isEnabled());
        voucher = voucherRepository.updateVoucher(voucher);
        return dtoMapper.map(voucher, VoucherResp.class);
    }

    @Override
    public Map<String, Object> deleteVoucher(VoucherDeleteForm form) {
        var voucher = voucherRepository.getVoucherById(Collections.emptyList(), form.getId());
        voucherRepository.deleteVoucher(voucher);
        Map<String, Object> result = new HashMap<>(2);
        result.put("status", HttpStatus.OK.value());
        result.put("messages", new String[]{"Delete successfully"});
        return result;
    }

    @Override
    public Collection<VoucherResp> getVouchers() {
        var response = voucherRepository.getListVoucher(Pageable.unpaged(), Sort.unsorted());
        return dtoMapper.map(response, VoucherResp.class);
    }

    @Override
    public Collection<VoucherResp> getVoucherSByStatus(boolean status) {
        var statusWhere = Where.builder().attribute(Voucher_.ACTIVE).value(status).operator(Operator.EQ).build();
        var result = voucherRepository.getVouchers(Collections.emptyList(), Collections.emptyList(), Collections.singletonList(statusWhere));
        return dtoMapper.map(result, VoucherResp.class);
    }

    @Override
    public Collection<VoucherResp> search(VoucherSearchForm form) {
        var context = entityFactory.getEntityContext(Voucher.class);
        var ids = context.search(form.getQuery());
        var result = voucherRepository.getVouchersByIds(ids);
        return dtoMapper.map(result, VoucherResp.class);
    }
}
