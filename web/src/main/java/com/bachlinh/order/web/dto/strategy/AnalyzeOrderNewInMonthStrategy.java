package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.AnalyzeOrderNewInMonthResp;

@ActiveReflection
public class AnalyzeOrderNewInMonthStrategy extends AbstractDtoStrategy<AnalyzeOrderNewInMonthResp, AnalyzeOrderNewInMonthResp.ResultSet> {

    @ActiveReflection
    public AnalyzeOrderNewInMonthStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(AnalyzeOrderNewInMonthResp.ResultSet source, Class<AnalyzeOrderNewInMonthResp> type) {
        // Do nothing
    }

    @Override
    protected AnalyzeOrderNewInMonthResp doConvert(AnalyzeOrderNewInMonthResp.ResultSet source, Class<AnalyzeOrderNewInMonthResp> type) {
        var resp = new AnalyzeOrderNewInMonthResp();
        resp.setPointInFirstWeek(new AnalyzeOrderNewInMonthResp.DataPoint(source.getFirst(), source.getSecond()));
        resp.setPointInSecondWeek(new AnalyzeOrderNewInMonthResp.DataPoint(source.getSecond(), source.getThird()));
        resp.setPointInThirdWeek(new AnalyzeOrderNewInMonthResp.DataPoint(source.getThird(), source.getFourth()));
        resp.setPointInLastWeek(new AnalyzeOrderNewInMonthResp.DataPoint(source.getFourth(), source.getLast()));
        return resp;
    }

    @Override
    protected void afterConvert(AnalyzeOrderNewInMonthResp.ResultSet source, Class<AnalyzeOrderNewInMonthResp> type) {
        // Do nothing
    }

    @Override
    public Class<AnalyzeOrderNewInMonthResp> getTargetType() {
        return AnalyzeOrderNewInMonthResp.class;
    }
}
