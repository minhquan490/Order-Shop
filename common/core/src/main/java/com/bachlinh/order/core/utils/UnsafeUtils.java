package com.bachlinh.order.core.utils;

import sun.misc.Unsafe;

import com.bachlinh.order.core.annotation.Reachable;
import com.bachlinh.order.core.exception.system.utils.UnsafeException;

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

    public static int getPointerAddress(long address) {
        return getUnsafe().getInt(address);
    }
}
