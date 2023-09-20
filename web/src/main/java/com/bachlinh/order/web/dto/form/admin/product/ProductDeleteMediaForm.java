package com.bachlinh.order.web.dto.form.admin.product;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ProductDeleteMediaForm {

    @JsonAlias("media_url")
    private String mediaUrl;

    public ProductDeleteMediaForm() {
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    @JsonAlias("media_url")
    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
