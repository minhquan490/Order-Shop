package com.bachlinh.order.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class RuntimeUtils {

    public static int getVersion() {
        String version = Runtime.class.getPackage().getImplementationVersion();
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    public static void computeMultiValueMap(String key, String value, Map<String, List<String>> target) {
        target.compute(key, (s, strings) -> {
            if (strings == null) {
                return withMessageNull(value);
            } else {
                withMessage(value, strings);
                return strings;
            }
        });
    }

    private static List<String> withMessageNull(String message) {
        var r = new ArrayList<String>();
        r.add(message);
        return r;
    }

    private static void withMessage(String message, List<String> store) {
        store.add(message);
    }
}
