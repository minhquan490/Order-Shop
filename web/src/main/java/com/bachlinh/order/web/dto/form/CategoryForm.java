package com.bachlinh.order.web.dto.form;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

@JsonRootName("category")
@Dto(forType = "com.bachlinh.order.entity.model.Category", packageName = "com.bachlinh.order.web.dto.form")
public class CategoryForm {

    @JsonAlias("category_id")
    @MappedDtoField(targetField = "id", outputJsonField = "category_id")
    private String id;

    @JsonAlias("category_name")
    private String name;

    @JsonAlias("page")
    private String page;

    @JsonAlias("page_size")
    private String pageSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
