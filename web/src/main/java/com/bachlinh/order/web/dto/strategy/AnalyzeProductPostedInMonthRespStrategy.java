package com.bachlinh.order.web.dto.strategy;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.dto.strategy.AbstractDtoStrategy;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.AnalyzeProductPostedInMonthResp;

@ActiveReflection
public class AnalyzeProductPostedInMonthRespStrategy extends AbstractDtoStrategy<AnalyzeProductPostedInMonthResp, AnalyzeProductPostedInMonthResp.ResultSet> {

    @ActiveReflection
    public AnalyzeProductPostedInMonthRespStrategy(DependenciesResolver dependenciesResolver, Environment environment) {
        super(dependenciesResolver, environment);
    }

    @Override
    protected void beforeConvert(AnalyzeProductPostedInMonthResp.ResultSet source, Class<AnalyzeProductPostedInMonthResp> type) {
        // Do nothing
    }

    @Override
    protected AnalyzeProductPostedInMonthResp doConvert(AnalyzeProductPostedInMonthResp.ResultSet source, Class<AnalyzeProductPostedInMonthResp> type) {
        var resp = new AnalyzeProductPostedInMonthResp();
        resp.setPointInFirstWeek(new AnalyzeProductPostedInMonthResp.DataPoint(source.getFirst(), source.getSecond()));
        resp.setPointInSecondWeek(new AnalyzeProductPostedInMonthResp.DataPoint(source.getSecond(), source.getThird()));
        resp.setPointInThirdWeek(new AnalyzeProductPostedInMonthResp.DataPoint(source.getThird(), source.getFourth()));
        resp.setPointInLastWeek(new AnalyzeProductPostedInMonthResp.DataPoint(source.getFourth(), source.getLast()));
        return resp;
    }

    @Override
    protected void afterConvert(AnalyzeProductPostedInMonthResp.ResultSet source, Class<AnalyzeProductPostedInMonthResp> type) {
        // Do nothing
    }

    @Override
    public Class<AnalyzeProductPostedInMonthResp> getDtoType() {
        return AnalyzeProductPostedInMonthResp.class;
    }
}
