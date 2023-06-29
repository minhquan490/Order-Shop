package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

import java.util.Collection;

@Getter
@Setter
@Dto(forType = "com.bachlinh.order.entity.model.Customer")
public class CustomerInformationResp {

    @MappedDtoField(targetField = "firstName", outputJsonField = "first_name")
    private String firstName;

    @MappedDtoField(targetField = "lastName", outputJsonField = "last_name")
    private String lastName;

    @MappedDtoField(targetField = "phoneNumber", outputJsonField = "phone")
    private String phoneNumber;

    @MappedDtoField(targetField = "email", outputJsonField = "email")
    private String email;

    @MappedDtoField(targetField = "gender", outputJsonField = "gender")
    private String gender;

    @MappedDtoField(targetField = "addressString", outputJsonField = "addresses")
    private Collection<String> address;
}
