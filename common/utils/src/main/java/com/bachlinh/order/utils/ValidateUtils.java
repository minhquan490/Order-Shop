package com.bachlinh.order.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ValidateUtils {
    private static final String PATTER = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
    private static final Pattern PATTERN = Pattern.compile(PATTER);

    public static boolean isEmailValidUsingRfc2822(String email) {
        return Rfc2822.validate(email);
    }

    public static boolean isEmailValidUsingRfc5322(String email) {
        return Rfc5322.validate(email);
    }

    public static boolean isPhoneValid(String phone) {
        return PATTERN.matcher(phone).matches();
    }

    private static class Rfc2822 {
        private static final String REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        private static final Pattern PATTERN = Pattern.compile(REGEX);

        static boolean validate(String email) {
            return PATTERN.matcher(email).matches();
        }
    }

    private static class Rfc5322 {
        private static final String REGEX = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        private static final Pattern PATTERN = Pattern.compile(REGEX);

        static boolean validate(String email) {
            return PATTERN.matcher(email).matches();
        }
    }
}
