package com.bachlinh.order.web.dto.form.admin.voucher;

import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;

public class VoucherUpdateForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("name")
    private String name;

    @JsonAlias("discount_percent")
    private int discountPercent;

    @JsonAlias("time_start")
    private String timeStart;

    @JsonAlias("time_end")
    private String timeEnd;

    @JsonAlias("content")
    private String content;

    @JsonAlias("cost")
    private int cost;

    @JsonAlias("is_enabled")
    private boolean enabled;

    public VoucherUpdateForm() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getDiscountPercent() {
        return this.discountPercent;
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

    public int getCost() {
        return this.cost;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @JsonAlias("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonAlias("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAlias("discount_percent")
    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
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

    @JsonAlias("cost")
    public void setCost(int cost) {
        this.cost = cost;
    }

    @JsonAlias("is_enabled")
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
