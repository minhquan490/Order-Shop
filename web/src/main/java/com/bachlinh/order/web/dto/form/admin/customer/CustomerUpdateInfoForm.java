package com.bachlinh.order.web.dto.form.admin.customer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Getter
@Setter
public class CustomerUpdateInfoForm implements ValidatedDto {

    @JsonAlias("id")
    private String id;

    @JsonAlias("first_name")
    private String firstName;

    @JsonAlias("last_name")
    private String lastName;

    @JsonAlias("phone")
    private String phoneNumber;

    @JsonAlias("email")
    private String email;

    @JsonAlias("gender")
    private String gender;

    @JsonAlias("role")
    private String role;

    @JsonAlias("order_point")
    private Integer orderPoint;

    @JsonAlias("activated")
    private Boolean activated;

    @JsonAlias("account_non_expired")
    private Boolean accountNonExpired;

    @JsonAlias("account_non_locked")
    private Boolean accountNonLocked;

    @JsonAlias("credentials_non_expired")
    private Boolean credentialsNonExpired;

    @JsonAlias("enabled")
    private Boolean enabled;

    @JsonAlias("addresses")
    private Address[] addresses;

    @Getter
    @Setter
    public static class Address {

        @JsonAlias("house_number")
        private String houseNumber;

        @JsonAlias("ward")
        private String wardId;

        @JsonAlias("district")
        private String districtId;

        @JsonAlias("province")
        private String provinceId;
    }
}
