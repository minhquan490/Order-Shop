package com.bachlinh.order.web.handler.rest.common.address;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.DistrictResp;
import com.bachlinh.order.web.service.common.DistrictService;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "districtListWithProvinceHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class DistrictListWithProvinceHandler extends AbstractController<Collection<DistrictResp>, Void> {

    private String url;
    private DistrictService districtService;

    @Override
    @ActiveReflection
    protected Collection<DistrictResp> internalHandler(Payload<Void> request) {
        String provinceId = getNativeRequest().getUrlQueryParam().getFirst("provinceId");
        if (!StringUtils.hasText(provinceId)) {
            throw new BadVariableException("Province id is required", getPath());
        }
        return districtService.getDistrictByProvince(provinceId);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (districtService == null) {
            districtService = resolver.resolveDependencies(DistrictService.class);
        }

    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.district.list-with-province");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}