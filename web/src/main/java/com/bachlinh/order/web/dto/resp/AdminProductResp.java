package com.bachlinh.order.web.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
}
