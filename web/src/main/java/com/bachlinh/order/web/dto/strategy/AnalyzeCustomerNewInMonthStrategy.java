package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.AnalyzeCustomerNewInMonthResp;

@ActiveReflection
public class AnalyzeCustomerNewInMonthStrategy extends AbstractDtoStrategy<AnalyzeCustomerNewInMonthResp, AnalyzeCustomerNewInMonthResp.ResultSet> {

    @ActiveReflection
    public AnalyzeCustomerNewInMonthStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(AnalyzeCustomerNewInMonthResp.ResultSet source, Class<AnalyzeCustomerNewInMonthResp> type) {
        // Do nothing
    }

    @Override
    protected AnalyzeCustomerNewInMonthResp doConvert(AnalyzeCustomerNewInMonthResp.ResultSet source, Class<AnalyzeCustomerNewInMonthResp> type) {
        var resp = new AnalyzeCustomerNewInMonthResp();
        resp.setPointInFirstWeek(new AnalyzeCustomerNewInMonthResp.DataPoint(source.getFirst(), source.getSecond()));
        resp.setPointInSecondWeek(new AnalyzeCustomerNewInMonthResp.DataPoint(source.getSecond(), source.getThird()));
        resp.setPointInThirdWeek(new AnalyzeCustomerNewInMonthResp.DataPoint(source.getThird(), source.getFourth()));
        resp.setPointInLastWeek(new AnalyzeCustomerNewInMonthResp.DataPoint(source.getFourth(), source.getLast()));
        return resp;
    }

    @Override
    protected void afterConvert(AnalyzeCustomerNewInMonthResp.ResultSet source, Class<AnalyzeCustomerNewInMonthResp> type) {
        // Do nothing
    }

    @Override
    public Class<AnalyzeCustomerNewInMonthResp> getDtoType() {
        return AnalyzeCustomerNewInMonthResp.class;
    }
}
