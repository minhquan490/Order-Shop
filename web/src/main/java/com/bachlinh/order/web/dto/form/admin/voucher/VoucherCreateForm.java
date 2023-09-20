package com.bachlinh.order.web.dto.form.admin.voucher;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class VoucherCreateForm implements ValidatedDto {

    @JsonAlias("name")
    private String name;

    @JsonAlias("discount_percent")
    private int discounterPercent;

    @JsonAlias("cost")
    private int cost;

    @JsonAlias("time_start")
    private String timeStart;

    @JsonAlias("time_end")
    private String timeEnd;

    @JsonAlias("content")
    private String content;

    @JsonAlias("is_enable")
    private boolean isEnable;

    public VoucherCreateForm() {
    }

    public String getName() {
        return this.name;
    }

    public int getDiscounterPercent() {
        return this.discounterPercent;
    }

    public int getCost() {
        return this.cost;
    }

    public String getTimeStart() {
        return this.timeStart;
    }

    public String getTimeEnd() {
        return this.timeEnd;
    }

    public String getContent() {
        return this.content;
    }

    public boolean isEnable() {
        return this.isEnable;
    }

    @JsonAlias("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAlias("discount_percent")
    public void setDiscounterPercent(int discounterPercent) {
        this.discounterPercent = discounterPercent;
    }

    @JsonAlias("cost")
    public void setCost(int cost) {
        this.cost = cost;
    }

    @JsonAlias("time_start")
    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    @JsonAlias("time_end")
    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    @JsonAlias("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonAlias("is_enable")
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }
}
