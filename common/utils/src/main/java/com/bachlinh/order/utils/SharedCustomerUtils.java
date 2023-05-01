package com.bachlinh.order.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class SharedCustomerUtils {
    private static final List<String> context = Collections.synchronizedList(new ArrayList<>());

    public static void addToSharedContext(String adminId) {
        context.add(adminId);
        Collections.sort(context);
    }

    public static void removeFromSharedContext(String adminId) {
        int index = Collections.binarySearch(context, adminId);
        context.remove(index);
    }

    public static Collection<String> getAllId() {
        return context;
    }
}
