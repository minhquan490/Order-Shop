package com.bachlinh.order.web.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.Dto;
import com.bachlinh.order.annotation.MappedDtoField;

import java.util.Collection;

@Dto(forType = "com.bachlinh.order.entity.model.Customer")
@Getter
@Setter
@NoArgsConstructor
public class CustomerResp {

    @MappedDtoField(targetField = "id", outputJsonField = "id")
    private String id;

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

    @MappedDtoField(targetField = "role", outputJsonField = "role")
    private String role;

    @MappedDtoField(targetField = "username", outputJsonField = "username")
    private String username;

    @MappedDtoField(targetField = "addressString", outputJsonField = "addresses")
    private Collection<String> address;

    @MappedDtoField(targetField = "activated", outputJsonField = "is_activated")
    private boolean activated;

    @MappedDtoField(targetField = "accountNonExpired", outputJsonField = "is_account_non_expired")
    private boolean accountNonExpired;

    @MappedDtoField(targetField = "accountNonLocked", outputJsonField = "is_account_non_locked")
    private boolean accountNonLocked;

    @MappedDtoField(targetField = "credentialsNonExpired", outputJsonField = "is_credentials_non_expired")
    private boolean credentialsNonExpired;

    @MappedDtoField(targetField = "enabled", outputJsonField = "is_enabled")
    private boolean enabled;

    @MappedDtoField(targetField = "picture", outputJsonField = "picture")
    private String picture;
}
