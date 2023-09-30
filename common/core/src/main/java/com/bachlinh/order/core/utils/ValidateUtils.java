package com.bachlinh.order.core.utils;

import java.util.regex.Pattern;

public final class ValidateUtils {

    private ValidateUtils() {
    }

    private static final String PATTER = "(84|0[3|5|7|8|9])+([0-9]{8})\\b";
    private static final Pattern PATTERN = Pattern.compile(PATTER);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern URL_PATTERN = Pattern.compile("((https?):\\/\\/)?(www.)?[a-z0-9]+(\\.[a-z]{2,}){1,3}(#?\\/?[a-zA-Z0-9#]+)*\\/?(\\?[a-zA-Z0-9-_]+=[a-zA-Z0-9-%]+&?)?$");
    private static final Pattern PRODUCT_SIZE_PATTERN = Pattern.compile("^[S|s]|[M|m]|[L|l]|([X|x]{1,}[L|l])$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}[-]?\\d{1,2}[-]?\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}[.]?\\d{1,6}$");

    public static boolean isEmailValidUsingRfc2822(String email) {
        if (email == null) {
            return false;
        }
        return Rfc2822.validate(email);
    }

    public static boolean isEmailValidUsingRfc5322(String email) {
        if (email == null) {
            return false;
        }
        return Rfc5322.validate(email);
    }

    public static boolean isPhoneValid(String phone) {
        if (phone == null) {
            return false;
        }
        return PATTERN.matcher(phone).matches();
    }

    public static boolean isUrlValid(String url) {
        if (url == null) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    public static boolean isSizeValid(String productSize) {
        if (productSize == null) {
            return false;
        }
        return PRODUCT_SIZE_PATTERN.matcher(productSize).matches();
    }

    public static boolean isValidDate(String testString) {
        if (testString == null) {
            return false;
        }
        return DATE_PATTERN.matcher(testString).matches();
    }

    public static boolean isNumber(String number) {
        if (number == null) {
            return false;
        }
        return NUMBER_PATTERN.matcher(number).matches();
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
