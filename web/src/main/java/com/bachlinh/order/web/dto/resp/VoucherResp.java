package com.bachlinh.order.web.dto.resp;

import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@Dto(forType = "com.bachlinh.order.entity.model.Voucher")
public class VoucherResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

    @MappedDtoField(targetField = "name", outputJsonField = "name")
    private String name;

    @MappedDtoField(targetField = "discountPercent", outputJsonField = "discount_percent")
    private int discountPercent;

    @MappedDtoField(targetField = "timeStart.toString", outputJsonField = "time_start")
    private String timeStart;

    @MappedDtoField(targetField = "timeExpired.toString", outputJsonField = "time_end")
    private String timeEnd;

    @MappedDtoField(targetField = "voucherContent", outputJsonField = "content")
    private String content;

    @MappedDtoField(targetField = "active", outputJsonField = "is_enable")
    private boolean enable;

    @MappedDtoField(targetField = "voucherCost", outputJsonField = "cost")
    private int cost;

    public VoucherResp() {
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

    public boolean isEnable() {
        return this.enable;
    }

    public int getCost() {
        return this.cost;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
