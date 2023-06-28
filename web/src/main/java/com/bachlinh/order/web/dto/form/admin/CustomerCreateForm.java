package com.bachlinh.order.web.dto.form.admin;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.validate.base.ValidatedDto;

@ActiveReflection
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
@Getter
@Setter(onMethod = @__({@ActiveReflection}))
public class CustomerCreateForm implements ValidatedDto {

    @JsonAlias("first_name")
    private String firstName;

    @JsonAlias("last_name")
    private String lastName;

    @JsonAlias("phone")
    private String phone;

    @JsonAlias("email")
    private String email;

    @JsonAlias("gender")
    private String gender;

    @JsonAlias("role")
    private String role;

    @JsonAlias("username")
    private String username;

    @JsonAlias("password")
    private String password;

    @JsonAlias("address")
    private AddressAttribute address;

    @ActiveReflection
    @NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
    @Getter
    @Setter(onMethod = @__({@ActiveReflection}))
    public static class AddressAttribute {

        @JsonAlias("province")
        private String province;

        @JsonAlias("district")
        private String district;

        @JsonAlias("ward")
        private String ward;

        @JsonAlias("house_address")
        private String houseAddress;
    }
}
