package com.bachlinh.order.web.handler.rest.admin.batch;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.BatchReportResp;
import com.bachlinh.order.web.service.common.BatchReportService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "batchReportListInYearHandler")
@Permit(roles = Role.ADMIN)
public class BatchReportListInYearHandler extends AbstractController<Collection<BatchReportResp>, Void> {
    private String url;
    private BatchReportService batchReportService;

    private BatchReportListInYearHandler() {
    }

    @Override
    public AbstractController<Collection<BatchReportResp>, Void> newInstance() {
        return new BatchReportListInYearHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<BatchReportResp> internalHandler(Payload<Void> request) {
        return batchReportService.getReportsInYear();
    }

    @Override
    protected void inject() {
        if (batchReportService == null) {
            batchReportService = resolveService(BatchReportService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.batch.list-in-year");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
