package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Objects;

public class ProductForm {
    @JsonAlias("product_id")
    private String id;
    @JsonAlias("product_name")
    private String name;
    @JsonAlias("product_price")
    private String price;
    @JsonAlias("product_size")
    private String size;
    @JsonAlias("product_color")
    private String color;
    @JsonAlias("product_taobao_url")
    private String taobaoUrl;
    @JsonAlias("product_description")
    private String description;
    @JsonAlias("product_enabled")
    private String enabled;
    @JsonAlias("product_categories")
    private String[] categories;
    @JsonAlias("product_order_point")
    private String orderPoint;
    @JsonIgnore
    private String page;
    @JsonIgnore
    private String pageSize;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTaobaoUrl() {
        return taobaoUrl;
    }

    public void setTaobaoUrl(String taobaoUrl) {
        this.taobaoUrl = taobaoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getOrderPoint() {
        return orderPoint;
    }

    public void setOrderPoint(String orderPoint) {
        this.orderPoint = orderPoint;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductForm that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(price, that.price) && Objects.equals(size, that.size) && Objects.equals(color, that.color) && Objects.equals(taobaoUrl, that.taobaoUrl) && Objects.equals(description, that.description) && Objects.equals(enabled, that.enabled) && Arrays.equals(categories, that.categories) && Objects.equals(orderPoint, that.orderPoint);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, price, size, color, taobaoUrl, description, enabled, orderPoint);
        result = 31 * result + Arrays.hashCode(categories);
        return result;
    }

    @Override
    public String toString() {
        return "ProductForm{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", taobaoUrl='" + taobaoUrl + '\'' +
                ", description='" + description + '\'' +
                ", enabled='" + enabled + '\'' +
                ", categories=" + Arrays.toString(categories) +
                '}';
    }
}
