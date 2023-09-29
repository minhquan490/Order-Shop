package com.bachlinh.order.utils;

import com.bachlinh.order.annotation.Reachable;
import com.bachlinh.order.exception.system.utils.UnsafeException;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Reachable(onClasses = UnsafeUtils.class, fieldNames = "theUnsafe")
public final class UnsafeUtils {
    private static final Unsafe INSTANCE;

    static {
        try {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            INSTANCE = (Unsafe) theUnsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnsafeException("Can not obtain the unsafe", e);
        }
    }

    private UnsafeUtils() {
    }

    public static Unsafe getUnsafe() {
        return INSTANCE;
    }

    public static <T> T allocateInstance(Class<T> type) throws InstantiationException {
        return type.cast(getUnsafe().allocateInstance(type));
    }
}
