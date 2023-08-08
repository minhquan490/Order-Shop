package com.bachlinh.order.web.handler.rest.admin.batch;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.BatchReportResp;
import com.bachlinh.order.web.service.common.BatchReportService;
import lombok.NoArgsConstructor;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "batchReportListInYearHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@Permit(roles = Role.ADMIN)
public class BatchReportListInYearHandler extends AbstractController<Collection<BatchReportResp>, Void> {
    private String url;
    private BatchReportService batchReportService;

    @Override
    @ActiveReflection
    protected Collection<BatchReportResp> internalHandler(Payload<Void> request) {
        return batchReportService.getReportsInYear();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (batchReportService == null) {
            batchReportService = resolver.resolveDependencies(BatchReportService.class);
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