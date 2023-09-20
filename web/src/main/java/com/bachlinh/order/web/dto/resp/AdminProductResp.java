package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminProductResp {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private String price;

    @JsonProperty("size")
    private String size;

    @JsonProperty("color")
    private String color;

    @JsonProperty("taobao_url")
    private String taobaoUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("orderPoint")
    private String orderPoint;

    @JsonProperty("enable")
    private String enable;

    @JsonProperty("pictures")
    private String[] pictures;

    @JsonProperty("categories")
    private String[] categories;

    public AdminProductResp() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPrice() {
        return this.price;
    }

    public String getSize() {
        return this.size;
    }

    public String getColor() {
        return this.color;
    }

    public String getTaobaoUrl() {
        return this.taobaoUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public String getOrderPoint() {
        return this.orderPoint;
    }

    public String getEnable() {
        return this.enable;
    }

    public String[] getPictures() {
        return this.pictures;
    }

    public String[] getCategories() {
        return this.categories;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("price")
    public void setPrice(String price) {
        this.price = price;
    }

    @JsonProperty("size")
    public void setSize(String size) {
        this.size = size;
    }

    @JsonProperty("color")
    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty("taobao_url")
    public void setTaobaoUrl(String taobaoUrl) {
        this.taobaoUrl = taobaoUrl;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("orderPoint")
    public void setOrderPoint(String orderPoint) {
        this.orderPoint = orderPoint;
    }

    @JsonProperty("enable")
    public void setEnable(String enable) {
        this.enable = enable;
    }

    @JsonProperty("pictures")
    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    @JsonProperty("categories")
    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}
