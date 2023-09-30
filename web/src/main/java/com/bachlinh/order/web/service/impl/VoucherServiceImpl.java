package com.bachlinh.order.web.service.impl;

import com.bachlinh.order.core.annotation.ServiceComponent;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.utils.ValidateUtils;
import com.bachlinh.order.dto.DtoMapper;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.MessageSetting;
import com.bachlinh.order.entity.model.Voucher;
import com.bachlinh.order.entity.model.Voucher_;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.handler.service.AbstractService;
import com.bachlinh.order.handler.service.ServiceBase;
import com.bachlinh.order.repository.MessageSettingRepository;
import com.bachlinh.order.repository.VoucherRepository;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherCreateForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherDeleteForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherSearchForm;
import com.bachlinh.order.web.dto.form.admin.voucher.VoucherUpdateForm;
import com.bachlinh.order.web.dto.resp.CustomerAssignmentVouchersResp;
import com.bachlinh.order.web.dto.resp.VoucherResp;
import com.bachlinh.order.web.service.business.VoucherSearchService;
import com.bachlinh.order.web.service.common.VoucherService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;

@ServiceComponent
public class VoucherServiceImpl extends AbstractService implements VoucherService, VoucherSearchService {
    private final VoucherRepository voucherRepository;
    private final EntityFactory entityFactory;
    private final DtoMapper dtoMapper;
    private final MessageSettingRepository messageSettingRepository;

    private VoucherServiceImpl(DependenciesResolver resolver, Environment environment) {
        super(resolver, environment);
        this.voucherRepository = resolveRepository(VoucherRepository.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.dtoMapper = resolver.resolveDependencies(DtoMapper.class);
        this.messageSettingRepository = resolveRepository(MessageSettingRepository.class);
    }

    @Override
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
    public void deleteVoucher(VoucherDeleteForm form) {
        var voucher = voucherRepository.getVoucherById(Collections.emptyList(), form.getId());
        voucherRepository.deleteVoucher(voucher);
    }

    @Override
    public Collection<VoucherResp> getVouchers() {
        var response = voucherRepository.getListVoucher(Pageable.unpaged(), Sort.unsorted());
        return dtoMapper.map(response, VoucherResp.class);
    }

    @Override
    public Collection<VoucherResp> getVouchersByStatus(boolean status) {
        var statusWhere = Where.builder().attribute(Voucher_.ACTIVE).value(status).operation(Operation.EQ).build();
        var result = voucherRepository.getVouchers(Collections.emptyList(), Collections.emptyList(), Collections.singletonList(statusWhere));
        return dtoMapper.map(result, VoucherResp.class);
    }

    @Override
    public CustomerAssignmentVouchersResp getAssignVouchers(NativeRequest<?> request) {
        String customerId = getCustomerId(request, request.getUrl());
        long pageSize = getPageSize(request);
        long page = getPage(request);

        Collection<Voucher> vouchers = voucherRepository.getVouchersAssignToCustomer(customerId, page, pageSize);
        Collection<CustomerAssignmentVouchersResp.AssignmentVoucher> assignmentVouchers = dtoMapper.map(vouchers, CustomerAssignmentVouchersResp.AssignmentVoucher.class);
        Long totalVouchers = voucherRepository.countVoucherAssignToCustomer(customerId);

        CustomerAssignmentVouchersResp resp = new CustomerAssignmentVouchersResp();
        resp.setVouchers(assignmentVouchers);
        resp.setTotalVouchers(totalVouchers);
        resp.setPage(page);
        resp.setPageSize(pageSize);
        return resp;
    }

    @Override
    public Collection<VoucherResp> search(VoucherSearchForm form) {
        var context = entityFactory.getEntityContext(Voucher.class);
        var ids = context.search(form.getQuery());
        var result = voucherRepository.getVouchersByIds(ids);
        return dtoMapper.map(result, VoucherResp.class);
    }

    @Override
    public ServiceBase getInstance(DependenciesResolver resolver, Environment environment) {
        return new VoucherServiceImpl(resolver, environment);
    }

    @Override
    public Class<?>[] getServiceTypes() {
        return new Class[]{VoucherService.class, VoucherSearchService.class};
    }

    private String getCustomerId(NativeRequest<?> nativeRequest, String path) {
        String customerId = nativeRequest.getUrlQueryParam().getFirst("customerId");
        if (!StringUtils.hasText(customerId)) {
            MessageSetting messageSetting = messageSettingRepository.getMessageById("MSG-000008");
            String errorContent = MessageFormat.format(messageSetting.getValue(), "Customer");
            throw new ResourceNotFoundException(errorContent, path);
        }
        return customerId;
    }

    private long getPageSize(NativeRequest<?> nativeRequest) {
        String pageSizeRequestParam = nativeRequest.getUrlQueryParam().getFirst("pageSize");
        if (ValidateUtils.isNumber(pageSizeRequestParam)) {
            return Long.parseLong(pageSizeRequestParam);
        } else {
            return 50L;
        }
    }

    private long getPage(NativeRequest<?> nativeRequest) {
        String pageRequestParam = nativeRequest.getUrlQueryParam().getFirst("page");
        if (ValidateUtils.isNumber(pageRequestParam)) {
            return Long.parseLong(pageRequestParam);
        } else {
            return 1L;
        }
    }
}
